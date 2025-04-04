package com.ProductManagement.services;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.service.ModelService;
import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

public class CreateProduct {

    private static final String MODULE = CreateProduct.class.getName();

    public static Map<String, Object> createProduct(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String productName = (String) context.get("productName");
        String productCategoryId = (String) context.get("productCategoryId");
        BigDecimal price = (BigDecimal) context.get("price");

        try {
            List<GenericValue> existingProducts = EntityQuery.use(delegator)
                    .from("FindProductView")
                    .where("productName", productName)
                    .queryList();

            if (UtilValidate.isNotEmpty(existingProducts)) {
                Debug.logWarning("Product with name " + productName + " already exists.", MODULE);
                result.put(ModelService.ERROR_MESSAGE, "Product with name " + productName + " already exists.");
                return result;
            }

            String productId = delegator.getNextSeqId("Product");
            GenericValue product = delegator.makeValue("Product");
            product.set("productId", productId);
            product.set("productName", productName);
            product.set("productTypeId", "FINISHED_GOOD");
            product.set("internalName",productName);
            product.set("isVirtual", "Y");
            product.set("isVariant", "N");
            
            delegator.create(product);
            Debug.logInfo("Created Product with ID: " + productId + ", Name: " + productName, MODULE);

            GenericValue productPrice = delegator.makeValue("ProductPrice");
            productPrice.set("productId", productId);
            productPrice.set("productPriceTypeId", "LIST_PRICE");
            productPrice.set("productPricePurposeId", "PURCHASE");
            productPrice.set("currencyUomId", "USD");
            productPrice.set("productStoreGroupId", "_NA_");
            productPrice.set("fromDate",  new java.sql.Timestamp(System.currentTimeMillis()));
            productPrice.set("price", price);
            delegator.create(productPrice);
            Debug.logInfo("Created ProductPrice   Price: " + price, MODULE);

            result.put("successMessage", "Product created successfully with ID: " + productId);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error creating product", MODULE);
            result = ServiceUtil.returnError("Error creating product: " + e.getMessage());
        }

        return result;
    }
}