import {Component} from "@angular/core";
import {HttpService} from "../HttpService";
@Component({
  selector: 'login-component',
  template: require('./login-component.html'),
  styles: [require('./login-component.css')],
})
export class LoginComponent {
  registration: boolean = false;
  enterButtonEnabled: boolean = false;

  fieldEnterLogin: string = '';
  fieldEnterPassword: string = '';

  constructor(private httpService: HttpService) {}

  forgotPassword() {
    window.alert("Плакать");
  }

  updateEnterButton() {
    this.enterButtonEnabled = !!this.fieldEnterLogin && !!this.fieldEnterPassword;
    console.log("this.enterButtonEnabled = " + this.enterButtonEnabled)
  }

  enterButtonClicked() {
    window.alert("Войти");
  }
}