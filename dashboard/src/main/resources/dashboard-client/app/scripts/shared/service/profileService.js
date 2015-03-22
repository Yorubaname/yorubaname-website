'use strict';

/**
 * Service that gets profile information
 */

var profileService = function() {

  this.getProfileInfo = function () {
    //Stubbing this for now
    return {
      profilePix : "",
      email: "stubemail@stub.com",
      role: "stub role",
      contributions: "stub contribution"
    };
  };


};

angular.module('dashboardappApp').service('profileService', profileService);
