package com.ProductManagement.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;

public class UpdateProductEvent {

    public static final String MODULE = UpdateProductEvent.class.getName();

    public static String updateProductEvent(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        String productId = request.getParameter("productId");
        String price = request.getParameter("price");
        String oldFeatureId = request.getParameter("oldFeatureId");
        String newFeatureId = request.getParameter("newFeatureId");

        if (UtilValidate.isEmpty(productId) ) {
            String errMsg = "ProductId is required fields on the form and can't be empty.";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            Debug.logInfo("=======Updating Product record in event using service updateProductService=========", MODULE);
            dispatcher.runSync("UpdateProductService", UtilMisc.toMap("productId", productId,
                    "price", price, "oldFeatureId", oldFeatureId,"newFeatureId",newFeatureId,"userLogin", userLogin));
        } catch (GenericServiceException e) {
            String errMsg = "Unable to update records in Product entity: " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        request.setAttribute("_EVENT_MESSAGE_", "Product updated succesfully.");
        return "success";
    }
}