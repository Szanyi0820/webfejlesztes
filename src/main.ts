// main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import {routes} from "./app/app.routes";
import {provideRouter} from "@angular/router";


bootstrapApplication(AppComponent, {
  providers: [provideHttpClient(),provideAnimations(),provideRouter([...routes])]
}).catch(err => console.error(err));
