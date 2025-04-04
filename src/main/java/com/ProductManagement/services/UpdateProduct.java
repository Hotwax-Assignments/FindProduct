package com.ProductManagement.services;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
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

public class UpdateProduct {

    private static final String MODULE = UpdateProduct.class.getName();

    public static Map<String, Object> updateProduct(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String productId = (String) context.get("productId");
        BigDecimal newPrice = (BigDecimal) context.get("price");
        String oldFeatureID = (String) context.get("oldFeatureId");
        String newFeatureId = (String) context.get("newFeatureId");
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        try 
        {
            List<GenericValue> existingProducts = EntityQuery.use(delegator)
                                                             .from("Product")
                                                             .where("productId", productId)
                                                             .queryList();

            if (UtilValidate.isEmpty(existingProducts)) 
            {
                Debug.logWarning("Product with ID " + productId + " does not exist.", MODULE);
                result.put(ModelService.ERROR_MESSAGE, "Product with ID " + productId + " does not exist.");
                return result;
            }

            if (newPrice != null) 
            {
                GenericValue oldPriceRecord = EntityQuery.use(delegator)
                                                         .from("ProductPrice")
                                                         .where("productId", productId)
                                                         .orderBy("-fromDate")
                                                         .queryFirst(); //as there can be multiple records thus we will access the first one on the basis of date

                if (oldPriceRecord != null) {
                    oldPriceRecord.set("thruDate", currentTimestamp);
                    oldPriceRecord.store();
                    Debug.logInfo("Updated thruDate for old ProductPrice for Product ID: " + productId, MODULE);

                    GenericValue newPriceRecord = delegator.makeValue("ProductPrice");
                    newPriceRecord.set("productId", productId);
                    newPriceRecord.set("productPriceTypeId", "LIST_PRICE");
                    newPriceRecord.set("productPricePurposeId", "PURCHASE");
                    newPriceRecord.set("currencyUomId", "USD");
                    newPriceRecord.set("productStoreGroupId", "_NA_");
                    newPriceRecord.set("fromDate", currentTimestamp);
                    newPriceRecord.set("price", newPrice);
                    delegator.create(newPriceRecord);
                    Debug.logInfo("Created new ProductPrice for Product ID: " + productId + ", New Price: " + newPrice, MODULE);
                } 
                else Debug.logWarning("No price record found for Product ID " + productId, MODULE);
            }

            if (oldFeatureID != null) {
                List<GenericValue> oldFeatures = EntityQuery.use(delegator)
                                                            .from("ProductFeatureAppl")
                                                            .where("productId", productId, "productFeatureId", oldFeatureID)
                                                            .queryList();

                for (GenericValue feature : oldFeatures) {
                    feature.set("thruDate", currentTimestamp);
                    delegator.store(feature);
                    Debug.logInfo("Updated thruDate for old Product Feature for Product ID: " + productId, MODULE);

                    if (UtilValidate.isNotEmpty(newFeatureId)) {
                        GenericValue newFeature = delegator.makeValue("ProductFeatureAppl");
                        newFeature.set("productId", productId);
                        newFeature.set("productFeatureId", newFeatureId);
                        newFeature.set("productFeatureApplTypeId", "SELECTABLE_FEATURE");
                        newFeature.set("fromDate", currentTimestamp);
                        delegator.create(newFeature);
                        Debug.logInfo("Created new Product Feature for Product ID: " + productId + ", Feature ID: " + newFeatureId, MODULE);
                    } else {
                        Debug.logWarning("New feature ID cannot be null or empty for Product ID: " + productId, MODULE);
                        result.put(ModelService.ERROR_MESSAGE, "New feature ID cannot be null or empty.");
                        return result;
                    }
                }
            }

            result.put("successMessage", "Product updated successfully with ID: " + productId);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error updating product", MODULE);
            result = ServiceUtil.returnError("Error updating product: " + e.getMessage());
        }

        return result;
    }
}