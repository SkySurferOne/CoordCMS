import { Injectable }    from '@angular/core';
import { Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

@Injectable()
export class CoordCmsService {
  private apiUrl = 'http://0.0.0.0:9000';
  private sectionsUrl = `${this.apiUrl}/api/pages/4/sections`;
  private sectionsCache = null;

  constructor(private http: Http) { }

  getSections(): Promise<Array<Object>> {
    return this.sectionsCache ? Promise.resolve(this.sectionsCache) :
      this.http.get(this.sectionsUrl)
        .toPromise()
        .then(response => {
          this.sectionsCache = response.json();
          return this.sectionsCache;
        })
        .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }

}
