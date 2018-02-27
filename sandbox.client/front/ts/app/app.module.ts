import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main_form/main_form.component";
import {HttpService} from "./HttpService";
import {ClientsListComponent} from "./clients_list/clients_list.component";
import {PagerService} from "./PagerService";
import {ClientFormComponent} from "./client_form/client_form.component";

@NgModule({
  imports: [
    BrowserModule, HttpModule, JsonpModule, FormsModule, ReactiveFormsModule
  ],
  declarations: [
    RootComponent, LoginComponent, MainFormComponent, ClientsListComponent, ClientFormComponent
  ],
  bootstrap: [RootComponent],
  providers: [HttpService, PagerService],
  entryComponents: [],
})
export class AppModule {
}