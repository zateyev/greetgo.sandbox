import {Component} from "@angular/core";

@Component({
  selector: 'root-component',
  template: `
    <login-component *ngIf="mode == 'login'"></login-component>
    <main-form-component *ngIf="mode == 'main-form'"></main-form-component>
  `
})
export class RootComponent {
  mode: string = "login"
}
