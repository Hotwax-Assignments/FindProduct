
package com.ProductManagement.services;

import java.util.Map;
import java.util.List;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.service.ModelService;

public class AssocProductToVirtual {

    private static final String MODULE = AssocProductToVirtual.class.getName();

    public static Map<String, Object> assocProductToVirtual(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String productId = (String) context.get("productId");
        String virtualProductId = (String) context.get("virtualProductId");

        try {
            List<GenericValue> existingProducts = EntityQuery.use(delegator)
                    .from("Product")
                    .where("productId", productId)
                    .queryList();

            List<GenericValue> existingVirtualProducts = EntityQuery.use(delegator)
                    .from("Product")
                    .where("productId", virtualProductId)
                    .queryList();

            if (UtilValidate.isEmpty(existingProducts)) {
                Debug.logWarning("Product with ID " + productId + " does not exist.", MODULE);
                result.put(ModelService.ERROR_MESSAGE, "Product with ID " + productId + " does not exist.");
                return result;
            }

            if (UtilValidate.isEmpty(existingVirtualProducts)) {
                Debug.logWarning("Virtual Product with ID " + virtualProductId + " does not exist.", MODULE);
                result.put(ModelService.ERROR_MESSAGE, "Virtual Product with ID " + virtualProductId + " does not exist.");
                return result;
            }

            GenericValue productAssoc = delegator.makeValue("ProductAssoc");
            productAssoc.set("productId", productId);
            productAssoc.set("productIdTo", virtualProductId);
            productAssoc.set("productAssocTypeId", "PRODUCT_VARIANT");
            productAssoc.set("fromDate",  new java.sql.Timestamp(System.currentTimeMillis()));
            delegator.create(productAssoc);

            Debug.logInfo("Created virtual association between Product ID: " + productId + " and Virtual Product ID: " + virtualProductId, MODULE);
            result.put("successMessage", "Successfully associated Product ID: " + productId + " with Virtual Product ID: " + virtualProductId);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error associating products", MODULE);
            result = ServiceUtil.returnError("Error associating products: " + e.getMessage());
        }

        return result;
    }
}
