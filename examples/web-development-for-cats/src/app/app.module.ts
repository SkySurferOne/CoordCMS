import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpModule}    from '@angular/http';

import {AppComponent} from './app.component';
import {HeaderComponent} from './header.component';
import {AboutComponent} from './about.component';
import {PriceComponent} from './price.component';
import {ContactComponent} from './contact.component';

import {CoordCmsService} from './coord-cms.service';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    AboutComponent,
    PriceComponent,
    ContactComponent
  ],
  imports: [
    BrowserModule,
    HttpModule
  ],
  providers: [CoordCmsService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
