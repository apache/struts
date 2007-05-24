function clearErrorMessages(form) {
    clearErrorMessagesXHTML(form);
}

function clearErrorMessagesXHTML(form) {

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
    clearErrorLabelsXHTML(form);
}

function clearErrorLabelsXHTML(form) {
    // set all labels back to the normal class
    var elements = form.elements;
    for (var i = 0; i < elements.length; i++) {
        var e = elements[i];
        //parent could be a row, or a cell
        var parent = e.parentNode.parentNode;
         //if labelposition is 'top' the label is on the row above
        if(parent.cells) {
          var labelRow = parent.cells.length > 1 ? parent : StrutsUtils.previousElement(parent, "tr");
          var cells = labelRow.cells;
          if (cells && cells.length >= 1) {
              var label = cells[0].getElementsByTagName("label")[0];
              if (label) {
                  label.setAttribute("class", "label");
                  label.setAttribute("className", "label"); //ie hack cause ie does not support setAttribute
              }
          }
        }
    }

}

function addError(e, errorText) {
    addErrorXHTML(e, errorText);
}

function addErrorXHTML(e, errorText) {
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

        // update the label too
        //if labelposition is 'top' the label is on the row above
        var labelRow = row.cells.length > 1 ? row : StrutsUtils.previousElement(tr, "tr");
        var label = labelRow.cells[0].getElementsByTagName("label")[0];
        label.setAttribute("class", "errorLabel");
        label.setAttribute("className", "errorLabel"); //ie hack cause ie does not support setAttribute
    } catch (e) {
        alert(e);
    }
}
