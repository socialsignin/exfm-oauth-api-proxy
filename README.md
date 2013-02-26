exfm-oauth-api-proxy
====================

This webapp is the beginnings of an OAuth proxy to Ex.Fm's API - originally created from a baseline of the Sparklr2 sample application in the Spring Security OAuth project (https://github.com/SpringSource/spring-security-oauth ).

I created this project so I could test the use of the spring-social-exfm module within Spring Social enabled webapps, as
Ex.Fm's current API does yet not support OAuth.

This project is currently deployed to http://exfmproxy.socialsignin.org, with clientids, secrets and redirecturls configured
to support the use of spring-social-exfm in the following sites:

http://api.cloudplaylists.com
http://socialsignin.org

A select number of operations of Ex.Fm's API are simply proxied by this application, with support for OAuth2 added to enable
authenticated methods to be accessed by supplying an access token instead of username/password.

The access token can be obtained through an OAuth2 authentication dance, supported by the following endpoints:

exfm.oauthTokenUrl=/oauth/token
exfm.oauthAuthorizeUrl=/oauth/authorize

with the main API base url mirroring that provided by the official Ex.Fm API:

exfm.oauthApiBaseUrl=/api/v3

Client Ids, Secrets and Redirect Uris are configured in the oauth:client-details-service element in spring-config.xml.

The ExFmUserDetailsService and ExFmUserPasswordService components within this project would be components that Ex.Fm would
implement should this code be used in an environment with access to Ex.Fm's user details store.

As I don't have access to such a user details store, I've implemented "workaround" versions of these components which verify
username/password combinations using Ex.Fm's API itself.
