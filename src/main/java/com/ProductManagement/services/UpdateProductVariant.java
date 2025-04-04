package com.ProductManagement.services;

import java.util.Map;
import java.util.List;
import java.sql.Timestamp;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.service.ModelService;

public class UpdateProductVariant {

    private static final String MODULE = UpdateProductVariant.class.getName();

    public static Map<String, Object> updateProductVariant(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String productId = (String) context.get("productId");
        String oldVariantId = (String) context.get("oldVariantId");
        String newVariantId = (String) context.get("newVariantId");

        try {
            List<GenericValue> existingAssociations = EntityQuery.use(delegator)
                    .from("ProductAssoc")
                    .where("productId", productId, "productIdTo", oldVariantId, "productAssocTypeId", "PRODUCT_VARIANT")
                    .queryList();

            if (UtilValidate.isEmpty(existingAssociations)) {
                Debug.logWarning("No existing virtual-variant relationship found for Product ID: " + productId + " and Variant ID: " + oldVariantId, MODULE);
                result.put(ModelService.ERROR_MESSAGE, "No existing virtual-variant relationship found for Product ID: " + productId + " and Variant ID: " + oldVariantId);
                return result;
            }

            for (GenericValue association : existingAssociations) {
                association.set("thruDate",  new java.sql.Timestamp(System.currentTimeMillis()));
                delegator.store(association);
                Debug.logInfo("Updated thruDate for old association between Product ID: " + productId + " and Variant ID: " + oldVariantId, MODULE);
            }

            GenericValue newAssociation = delegator.makeValue("ProductAssoc");
            newAssociation.set("productId", productId);
            newAssociation.set("productIdTo", newVariantId);
            newAssociation.set("productAssocTypeId", "PRODUCT_VARIANT");
            newAssociation.set("fromDate", new Timestamp(System.currentTimeMillis())); // Set fromDate to now
            delegator.create(newAssociation);

            Debug.logInfo("Created new virtual-variant association between Product ID: " + productId + " and New Variant ID: " + newVariantId, MODULE);
            result.put("successMessage", "Successfully updated variant association from Variant ID: " + oldVariantId + " to New Variant ID: " + newVariantId);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error updating product variant", MODULE);
            result = ServiceUtil.returnError("Error updating product variant: " + e.getMessage());
        }

        return result;
    }
}