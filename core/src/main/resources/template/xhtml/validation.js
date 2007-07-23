function clearErrorMessages(form) {

    var table = form.childNodes[1];
    if( typeof table == "undefined" ) {
        table = form.childNodes[0];
    }

    // clear out any rows with an "errorFor" attribute
    var rows = table.rows;
    var rowsToDelete = new Array();
    if (rows == null){
        return;
    }

    for(var i = 0; i < rows.length; i++) {
        var r = rows[i];
        if (r.getAttribute("errorFor")) {
            rowsToDelete.push(r);
        }
    }

    // now delete the rows
    for (var i = 0; i < rowsToDelete.length; i++) {
        var r = rowsToDelete[i];
        table.deleteRow(r.rowIndex);
        //table.removeChild(rowsToDelete[i]); 
    }
}

function clearErrorLabels(form) {
    // set all labels back to the normal class
    var elements = form.elements;
    for (var i = 0; i < elements.length; i++) {
        var e = elements[i];
        var cells = e.parentNode.parentNode.cells;
        if (cells && cells.length >= 2) {
            var label = cells[0].getElementsByTagName("label")[0];
            if (label) {
                label.setAttribute("class", "label");
                label.setAttribute("className", "label"); //ie hack cause ie does not support setAttribute
            }
        }
    }

}

function addError(e, errorText) {
    try {
        // clear out any rows with an "errorFor" of e.id
        var row = e.parentNode.parentNode;
        var table = row.parentNode;
        var error = document.createTextNode(errorText);
        var tr = document.createElement("tr");
        var td = document.createElement("td");
        var span = document.createElement("span");
        td.align = "center";
        td.valign = "top";
        td.colSpan = 2;
        span.setAttribute("class", "errorMessage");
        span.setAttribute("className", "errorMessage"); //ie hack cause ie does not support setAttribute
        span.appendChild(error);
        td.appendChild(span);
        tr.appendChild(td);
        tr.setAttribute("errorFor", e.id);;
        table.insertBefore(tr, row);

        // updat the label too
        var label = row.cells[0].getElementsByTagName("label")[0];
        label.setAttribute("class", "errorLabel");
        label.setAttribute("className", "errorLabel"); //ie hack cause ie does not support setAttribute
    } catch (e) {
        alert(e);
    }
}
