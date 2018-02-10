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

@NgModule({
  imports: [
    BrowserModule, HttpModule, JsonpModule, FormsModule, ReactiveFormsModule
  ],
  declarations: [
    RootComponent, LoginComponent, MainFormComponent, ClientsListComponent
  ],
  bootstrap: [RootComponent],
  providers: [HttpService, PagerService],
  entryComponents: [],
})
export class AppModule {
}