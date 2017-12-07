import {Component, OnInit} from "@angular/core";
import {HttpService} from "./HttpService";
import {AuthInfo} from "../model/AuthInfo";

@Component({
  selector: 'root-component',
  template: `
    <login-component
      *ngIf="mode == 'login'"
      (finish)="startApp()"
    ></login-component>
    <list-component
      *ngIf="mode == 'list'"
      (exit)="exit()"
    ></list-component>
    <main-form-component
      *ngIf="mode == 'main-form'"
      [cou] = "cou"
      (exit)="exit()"
    ></main-form-component>


    <div style="margin-top: 25%;" class="text-center" *ngIf="mode == 'init'">
      <span><img src="/img/load.gif" alt=""></span>
    </div>
  `
})
export class RootComponent implements OnInit {
  mode: string = "login";

  cou:number = 5;
  constructor(private httpService: HttpService) {}

  ngOnInit(): void {
    this.mode = 'init';
    this.startApp();
  }

  startApp() {
    if (!this.httpService.token) {
      this.mode = 'login';
      return;
    }

    this.httpService.get("/auth/info").toPromise().then(result => {
      let userInfo = result.json() as AuthInfo;
      if (userInfo.pageSize) this.httpService.pageSize = userInfo.pageSize;
      (<any>window).document.title = userInfo.appTitle;
      this.mode = 'list';
    }, error => {
      console.log(error);
      this.mode = "login";
    });

  }

  exit() {
    this.httpService.token = null;
    this.mode = 'login';
  }
}
