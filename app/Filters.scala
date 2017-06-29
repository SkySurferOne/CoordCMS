import javax.inject.Inject

import play.api.http.DefaultHttpFilters

import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter
import play.filters.hosts.AllowedHostsFilter
import play.filters.cors.CORSFilter

/**
 * Add the following filters by default to all projects
 * 
 * https://www.playframework.com/documentation/latest/ScalaCsrf 
 * https://www.playframework.com/documentation/latest/AllowedHostsFilter
 * https://www.playframework.com/documentation/latest/SecurityHeaders
 *
 * http://semisafe.com/coding/2015/08/03/play_basics_csrf_protection.html
 */
class Filters @Inject() (
  //csrfFilter: CSRFFilter,
  allowedHostsFilter: AllowedHostsFilter,
  securityHeadersFilter: SecurityHeadersFilter,
  corsFilter: CORSFilter
) extends DefaultHttpFilters(
  //csrfFilter,
  allowedHostsFilter, 
  securityHeadersFilter,
  corsFilter
)
