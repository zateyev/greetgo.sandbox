import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";

@NgModule({
  imports: [
    BrowserModule, HttpModule, JsonpModule,
    FormsModule, JsonpModule
  ],
  declarations: [
    RootComponent,
  ],
  bootstrap: [RootComponent],
  providers: [],
  entryComponents: [],
})
export class AppModule {}