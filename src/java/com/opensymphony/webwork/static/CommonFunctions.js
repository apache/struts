
/**
 *      Methods for the tabbed component
 */
var unselectedClass = "tab_default tab_unselected";
var unselectedContentsClass = "tab_contents_hidden";
var unselectedOverClass = "tab_default tab_unselected tab_unselected_over";
var selectedClass = "tab_default tab_selected";
var selectedContentsClass = "tab_contents_header";

function mouseIn(tab) {
    var className = tab.className;
    if (className.indexOf('unselected') > -1) {
        className = unselectedOverClass;
        tab.className = className;
    }
}

function mouseOut(tab) {
    var className = tab.className;
    if (className.indexOf('unselected') > -1) {
        className = unselectedClass;
        tab.className = className;
    }
}

/*
 * An object that represents a tabbed page.
 *
 * @param htmlId the id of the element that represents the tab page
 * @param remote whether this is a remote element and needs refreshing
 */
function TabContent( htmlId, remote ) {

    this.elementId = htmlId;
    this.isRemote = remote;
    var selected = false;
    var self = this;

    /*
     * Shows or hides this page depending on whether the visible
     * tab id matches this objects id.
     *
     * @param visibleTabId the id of the tab that was selected
     */
    this.updateVisibility = function( visibleTabId ) {
        var thElement = document.getElementById( 'tab_header_'+self.elementId );
        var tcElement = document.getElementById( 'tab_contents_'+self.elementId );
        if (!selected && visibleTabId==self.elementId) {
            thElement.className = selectedClass;
            tcElement.className = selectedContentsClass;
            self.selected = true;

        } else {
            thElement.className = unselectedClass;
            tcElement.className = unselectedContentsClass;
            self.selected = false;
        }
        if (self.isRemote==true && visibleTabId==self.elementId) {
            var rel = window['tab_contents_update_'+self.elementId];
            // If the first tab is a remote tab, rel is null on initial loading...
            //  so don't try to call a method that doesn't exist.  This is only
            //  for IE, and the workaround is to use a <ww:action name="" executeResults="true" />
            //  as the content of the DIV.
            if (rel.bind)
                rel.bind();
        }
    }

}

/**
 * Checks whether the current form include an ajax-ified submit button, if so
 * we return true (otherwise false).
 *
 * @param form the HTML form element to check
 */
function isAjaxFormSubmit( form ) {
    // we check whether this exists
    //      <INPUT type="submit" dojoattachevent="onClick: execute" dojoattachpoint="attachBtn" />
    var thisForm = document.getElementById(form.id);
    var matchUrl = /\s+dojoAttachPoint/;
    if( thisForm.innerHTML.match(matchUrl) ) {
        return false;
    }
    for( i=0; i<thisForm.elements.length; i++ ) {
        var field = thisForm.elements[i];
        if( field.type.toLowerCase()=='submit' ) {
            if( field.hasAttribute("dojoAttachPoint") && field.getAttribute("dojoAttachPoint")=="attachBtn" ) {
                return false;
            }
        }
    }
    return true;
}

/**  end tabbed component functions ******************************************************************/
