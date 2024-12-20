import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  constructor(private http: HttpClient) { }

  getCars(): Observable<Car[]> {
    return this.http.get<Car[]>(this.apiUrl);
  }
  getOwners(): Observable<CarOwner[]> {
    return this.http.get<CarOwner[]>(this.apiUrlOwner);
  }

  addCar(car: Car): Observable<any> {
    return this.http.post(this.apiAddUrl, car);
  }
  addOwner(owner: CarOwner): Observable<any> {
    return this.http.post(this.apiAddUrlOwner, owner);
  }

  removeCar(car: Car): Observable<any> {
    return this.http.delete(`${this.apiRemoveUrl}/${car.properties.plateNumber}`);
  }
  removeOwner(owner: CarOwner): Observable<any> {
    return this.http.delete(`${this.apiRemoveUrlOwner}/${owner.properties.plateNumber}`);
  }
  updateCar(car: Car): Observable<any> {
    return this.http.put(`${this.apiUpdateUrl}/${car.properties.plateNumber}`, car);
  }
  updateOwner(owner: CarOwner): Observable<any> {
    return this.http.put(`${this.apiUpdateUrlOwner}/${owner.properties.plateNumber}`, owner);
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


