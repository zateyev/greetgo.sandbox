import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Headers, Http, Request, RequestOptionsArgs, Response} from "@angular/http";

@Injectable()
export class HttpService {

  constructor(private http: Http) {
  }

  private prefix(): string {
    return (<any>window).urlPrefix;
  }

  public url(urlSuffix: string): string {
    return this.prefix() + urlSuffix;
  }

  public requestStr(urlSuffix: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.request(this.url(urlSuffix), options);
  }

  public request(request: Request, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.request(request, options);
  }

  public get(urlSuffix: string, keyValue?: { [key: string]: string | number | null }): Observable<Response> {
    let post: string = '';

    if (keyValue) {

      let data = new URLSearchParams();
      let appended = false;
      for (let key in keyValue) {
        let value = keyValue[key];
        if (value) {
          data.append(key, value as string);
          appended = true;
        }
      }

      if (appended) post = '?' + data.toString();
    }

    return this.http.get(this.url(urlSuffix) + post);
  }

  public postDirect(urlSuffix: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.post(this.url(urlSuffix), body, options);
  }

  public post(urlSuffix: string, keyValue: { [key: string]: string | number | null }): Observable<Response> {
    let data = new URLSearchParams();
    for (let key in keyValue) {
      let value = keyValue[key];
      if (value) data.append(key, value as string);
    }

    let headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');

    return this.http.post(this.url(urlSuffix), data.toString(), {headers: headers});
  }

  public put(urlSuffix: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.post(this.url(urlSuffix), body, options);
  }

  public del(urlSuffix: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.delete(this.url(urlSuffix), options);
  }

  public patch(urlSuffix: string, body: any, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.patch(this.url(urlSuffix), body, options);
  }

  public head(urlSuffix: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.head(this.url(urlSuffix), options);
  }

  public options(urlSuffix: string, options?: RequestOptionsArgs): Observable<Response> {
    return this.http.options(this.url(urlSuffix), options);
  }
}
