// app.module.ts
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PaginatorModule } from 'primeng/paginator';
import { DropdownModule } from 'primeng/dropdown';
import { FormsModule } from '@angular/forms';  // Already present

import { SidebarModule } from 'primeng/sidebar';
import { InputNumberModule } from 'primeng/inputnumber';
import { CheckboxModule } from 'primeng/checkbox';
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {InputGroupModule} from "primeng/inputgroup";
import {SliderModule} from "primeng/slider";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";





@NgModule({
  declarations: [



  ],
  imports: [
    AppComponent,
    BrowserModule,
    HttpClientModule,
    TableModule,
    InputTextModule,
    ButtonModule,
    PaginatorModule,
    DropdownModule,
    FormsModule,
    SidebarModule,
    InputNumberModule,
    CheckboxModule,
    InputGroupAddonModule,
    InputGroupModule,
    SliderModule,
    BrowserAnimationsModule
  ],
  providers: [],
  exports: [


  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
