<form name='findProduct' method="post" action="<@ofbizUrl>findProduct</@ofbizUrl>">
    <div id="findProduct" class="screenlet styled-form">
        <div class="screenlet-body">
            <table class="basic-table" cellspacing='0'>
                <tr>
                    <td class='label'>Product id</td>
                    <td><input type='text' name='productId'/></td>
                </tr>
                <tr>
                    <td class='label'>Product Name</td>
                    <td><input type='text' name='productName'/></td>
                </tr>
                <tr>
                    <td class='label'>Feature type</td>
                    <td><input type='text' name='featureType'/></td>
                </tr>
                <tr>
                    <td class='label'>Price from</td>
                    <td><input type='text' name='priceFrom'/></td>
                </tr>
                <tr>
                    <td class='label'>Price to</td>
                    <td><input type='text' name='priceTo'/></td>
                </tr>
                <tr>
                    <td class="label"></td>
                    <td>
                        <input type="hidden" name="showAll" value="Y"/>
                        <input type='submit' value='${uiLabelMap.CommonFind}'/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</form>