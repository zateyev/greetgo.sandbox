import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main_form/main_form.component";
import {HttpService} from "./HttpService";
import {ClientListComponent} from "./clients/client-list.component";
import {ModalFormComponent} from "./forms/modal-form.component";

@NgModule({
  imports: [
    BrowserModule, HttpModule, JsonpModule, FormsModule
  ],
  declarations: [
    RootComponent, LoginComponent, MainFormComponent, ClientListComponent, ModalFormComponent
  ],
  bootstrap: [RootComponent],
  providers: [HttpService],
  entryComponents: [],
})
export class AppModule {
}