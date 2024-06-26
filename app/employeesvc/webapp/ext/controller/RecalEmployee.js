sap.ui.define([
    "sap/m/MessageToast"
], function(MessageToast) {
    'use strict';

    return {
        recalculateEmployees: function(oEvent) {
            MessageToast.show("Custom handler invoked.");
        }
    };
});
