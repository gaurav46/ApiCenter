import { Injectable } from '@angular/core';
import {environment} from '../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {Specification} from './models/specification';

@Injectable()
export class SpecificationStore {

  private urlRoot = environment.apiUrl + '/service';

  constructor(private http: HttpClient) { }

  public getYamlSpecification(serviceId: string, version: string) {
    const headers = new HttpHeaders({'Accept': 'application/yml'});
    return this.http.get<Specification>(this.urlRoot + '/' + serviceId + '/version/' + version, {headers})
      .catch((error: any) => throwError(error || 'Server error'));
  }

  public getSpecification(serviceId: string, version: string) {
    return this.http.get<Specification>(this.urlRoot + '/' + serviceId + '/version/' + version)
      .catch((error: any) => throwError(error || 'Server error'));
  }

  public getSpecificationDiff(serviceId: string, version1: string, version2: string) {
    const url = this.urlRoot + '/' + serviceId + '/diff/' + version1 + '/with/' + version2;
    return this.http.get(url)
      .catch((error: any) => throwError(error || 'Server error'));
  }

  public deleteSpecification(serviceId: string, version: string): Observable<Specification> {
    return this.http.delete<Specification>(this.urlRoot + '/' + serviceId + '/version/' + version)
      .catch((error: any) => throwError(error || 'Server error'));
  }
}
