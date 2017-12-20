import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";

@Component({
  selector: 'main-form-component',
  template: `
    <div>
      <h2>Main Form Component</h2>

      <button class="buttonStyle" (click)="exit.emit()">Выход</button>

      <div *ngIf="!userInfo">
        <button class="buttonStyle" [disabled]="!loadUserInfoButtonEnabled" (click)="loadUserInfoButtonClicked()">
          Загрузить данные пользователя
        </button>
        <div *ngIf="loadUserInfoError">
          {{loadUserInfoError}}
        </div>
      </div>
      
      <client-list></client-list>
      
    </div>`,
})
export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  constructor(private httpService: HttpService) {}

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    this.httpService.get("/auth/userInfo").toPromise().then(result => {
      this.userInfo = new UserInfo().assign(result.json() as UserInfo);
    }, error => {
      console.log(error);
      this.loadUserInfoButtonEnabled = true;
      this.loadUserInfoError = error;
      this.userInfo = null;
    });
  }
}
