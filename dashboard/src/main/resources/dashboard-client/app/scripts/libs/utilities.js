/* 
Utility Functions adapted from Code base
*/

function isEmpty(str) {
    return (!str || !/\S/.test(str) || 0 === str.length);
}

var hasOwnProperty = Object.prototype.hasOwnProperty;

function isEmptyObj(obj) {
    // null and undefined are "empty"
    if (obj == null) return true;
    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length > 0) return false;
    if (obj.length === 0) return true;
    // Otherwise, does it have any properties of its own?
    // Note that this doesn't handle
    // toString and valueOf enumeration bugs in IE < 9
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }
    return true;
}

