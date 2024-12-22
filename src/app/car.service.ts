import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Router} from "@angular/router";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class CarService {
  private apiUrl = 'http://localhost:8080/rest/car/list';
  private apiUrlOwner = 'http://localhost:8080/rest/car/owners/list';
  private apiAddUrl = 'http://localhost:8080/rest/car/add';
  private apiAddUrlOwner = 'http://localhost:8080/rest/car/owners/add';
  private apiRemoveUrl = 'http://localhost:8080/rest/car/delete';
  private apiRemoveUrlOwner = 'http://localhost:8080/rest/car/owners/delete';
  private apiUpdateUrl = 'http://localhost:8080/rest/car/update';
  private apiUpdateUrlOwner = 'http://localhost:8080/rest/car/owners/update';

  constructor(private authService: AuthService,private http: HttpClient, private router: Router) { }
  getCars(): Observable<Car[]> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<Car[]>(this.apiUrl, { headers });
  }
  getOwners(): Observable<CarOwner[]> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<CarOwner[]>(this.apiUrlOwner, { headers });
  }

  addCar(car: Car): Observable<any> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(this.apiAddUrl, car, { headers });
  }
  addOwner(owner: CarOwner): Observable<any> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(this.apiAddUrlOwner, owner, { headers });
  }

  removeCar(car: Car): Observable<any> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete(`${this.apiRemoveUrl}/${car.properties.plateNumber}`, { headers });
  }
  removeOwner(owner: CarOwner): Observable<any> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete(`${this.apiRemoveUrlOwner}/${owner.properties.plateNumber}`, { headers });
  }
  updateCar(car: Car): Observable<any> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put(`${this.apiUpdateUrl}/${car.properties.plateNumber}`, car, { headers });
  }
  updateOwner(owner: CarOwner): Observable<any> {
    const token = this.authService.getToken();

    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put(`${this.apiUpdateUrlOwner}/${owner.properties.plateNumber}`, owner, { headers });
  }
}



export interface Car {
  properties:{
  carType: {
    brandName: string;
    color: string;
    fuelType: string;
    maxSpeed: number;
    price: number;
    typeName: string;
  };
  factoryDate: string;
  plateNumber: string;
}}
export interface CarOwner {
  properties: {
    name:string;
    plateNumber: string;
    contactNumber:string;
    email:string;
  }
}


