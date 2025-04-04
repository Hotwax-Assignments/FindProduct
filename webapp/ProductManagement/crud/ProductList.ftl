<style>
   .tableStyle{
       border:1px solid black;
       padding:8px; 
       text-align:center;
   }
</style>



<table class="styled-table" style="border-collapse:collapse; width:100%" >
    <thead>
        <tr>
            <th  class="tableStyle">Product Id</th>
            <th class="tableStyle">Product name</th>
            <th class="tableStyle">Price</th>
            <th class="tableStyle">Product Feature id</th>
            <th class="tableStyle">Product Feature Type</th>
        </tr>
    </thead>

    <#if  parameters.productList?has_content>
    <tbody>
        <#list parameters.productList as result>
            <tr >
                <td  class="tableStyle">${result.get("productId")!""}</td>
                <td class="tableStyle">${result.get("productName")!""}</td>
                <td class="tableStyle">${result.get("price")!""}</td>
               <td class="tableStyle">${result.get("productFeatureId")!""}</td>
                <td class="tableStyle">${result.get("productFeatureTypeId")!""}</td>
            </tr>
        </#list>
    </tbody>
    <#else>
    <tbody>
        <tr><td colspan="9">No products found.</td></tr>
    </tbody>
    </#if>
</table>