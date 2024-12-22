import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import {CarService, Car, CarOwner} from './car.service';
import { FormsModule } from "@angular/forms";
import { TableModule } from 'primeng/table';
import {Button} from "primeng/button";
import {InputGroupModule} from "primeng/inputgroup";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {Component, OnInit} from "@angular/core";
import {SliderModule} from "primeng/slider";
import {CalendarModule} from "primeng/calendar";
import {AppModule} from "./app.module";
import {ToggleButtonModule} from "primeng/togglebutton";
import {InputSwitchModule} from "primeng/inputswitch";
import {response} from "express";
import {SidebarModule} from "primeng/sidebar";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {DialogModule} from "primeng/dialog";
import {AuthService} from "./auth.service";


@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [
    CommonModule,
    FormsModule,
    Button,
    TableModule,
    SliderModule,
    InputGroupAddonModule,
    InputGroupModule,
    ToggleButtonModule,
    InputSwitchModule,
    SidebarModule,
    DialogModule,


  ],
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  cars: Car[] = [];
  newCar: Car = this.resetNewCar();
  newOwner: CarOwner=this.resetNewOwner();
  sidebarVisible = false;
  visible=false;
  editCar: Car | null = null;
  carOwners:CarOwner[] =[];
  ownerToEdit: CarOwner|null = null;
  originalCar: Car | null = null; // To store original car data
  brandNameFilter: string = '';
  colorFilter: string = '';
  fuelTypeFilter: string = '';
  typeNameFilter: string = '';
  plateNumberFilter: string = '';
  NameFilter:string='';
  EmailFilter:string='';
  ContactNumberFilter:string='';
  PlateNumberFilterOwner:string='';
  maxSpeedFilter: number = 0; // A max sebesség szűrő
  priceFilter: number = 0; // Az ár szűrő
  minFactoryDate: Date | null = null;
  maxFactoryDate: Date | null = null;
  isLoggedIn = false; // Track login status
  username = ""; // Bind to the login form
  password = ""; // Bind to the login form
  registerUsername = "";
  registerPassword = "";
  registerEmail = "";
  role="";
  errorMessage: string | null = null;
  showCars: boolean = true; // Initialize to show cars table by default
  isAdmin:boolean=false;
  toggleOptions: any[] = [
    { label: 'Cars', value: true },
    { label: 'Car Owners', value: false }
  ];


  public originalOwner: CarOwner | null = null;
  public showDialog(){
    this.visible=!this.visible
  }

// Method to enable editing of an owner
  // Initialize minFactoryDate with null or a Date object

// Format a Date object to "yyyy-MM-dd" for display in the input field
  get formattedMinFactoryDate(): string | null {
    return this.minFactoryDate ? this.formatDateToString(this.minFactoryDate) : null;
  }
  get formattedMaxFactoryDate():string|null{
    return this.maxFactoryDate ? this.formatDateToString(this.maxFactoryDate):null;
  }

// Update minFactoryDate based on input changes

// Helper function to format a Date object as "yyyy-MM-dd"
  public formatDateToString(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  editOwner(owner: CarOwner): void {
    if (!this.originalOwner) {
      // Set originalOwner only when the user starts editing
      this.originalOwner = { ...owner };
    }
    console.log('Updated carOwners before cancel:', this.originalOwner.properties.contactNumber);
    // Cloning the selected owner to prevent shared references
    this.ownerToEdit = { ...owner };

  }

  enableEdit(car: Car): void {
    this.originalCar = { ...car };  // Save the original car data
    this.editCar = { ...car };      // Make a copy of the car for editing
  }



// Method to update the owner
  updateOwner(): void {
    if (this.ownerToEdit) {
      // Ensure that the updated values are correctly set
      console.log("Updating owner with plate number: ", this.ownerToEdit.properties.plateNumber);

      this.carService.updateOwner(this.ownerToEdit).subscribe(
        (response) => {
          console.log('Owner updated successfully:', response);
          this.fetchCarOwners()
          this.ownerToEdit = null;
          this.originalOwner = null;
        },
        error => {
          console.error('Error updating owner:', error);
        }
      );
    }
  }
  updateCar(): void {
    if (this.editCar) {
      this.carService.updateCar(this.editCar).subscribe(
        (response) => {
          console.log('Car updated successfully:', response);
          this.loadCars();
          this.editCar = null;
          this.originalCar = null;  // Clear original car data after saving
        },
        error => {
          console.error('Error updating car:', error);
        }
      );
    }
  }





// Method to cancel editing and restore the original owner data
  cancelEditOwner(): void {
    if (this.originalOwner) {
      // Refetch the owner data if needed (e.g., reload from the database)
      const index = this.carOwners.findIndex(owner => owner.properties.plateNumber === this.originalOwner?.properties.plateNumber);
      if (index !== -1) {
        this.ownerToEdit = { ...this.originalOwner };  // Restore original owner values
      }
    }
    this.ownerToEdit = null;  // Clear the owner edit state
    this.originalOwner = null;  // Clear the original owner data
    this.fetchCarOwners()
  }



  cancelEdit(): void {
    if (this.originalCar) {
      const index = this.cars.findIndex(car => car.properties.plateNumber === this.originalCar?.properties.plateNumber);
      if (index !== -1) {
        this.cars[index] = { ...this.originalCar };  // Restore original values
      }
    }
    this.editCar = null;
    this.originalCar = null;
    this.loadCars()
  }







  onMinDateChange(value: string): void {
    this.minFactoryDate = value ? new Date(value) : null;
  }

  onMaxDateChange(value: string): void {
    this.maxFactoryDate = value ? new Date(value) : null;
  }
  filteredCars: Car[] = [];
  filteredOwners:CarOwner[]=[]

  constructor(private carService: CarService,private authService: AuthService) {

  }

  ngOnInit(): void {
    localStorage.clear();
    this.isLoggedIn=false;

    }



  isRegistering = false;
  onLogin(): void {
    this.authService.login(this.username, this.password, this.role).subscribe(
      (response) => {
        this.authService.saveToken(response.token); // Save token
        this.isLoggedIn = true; // Update login status

        // After login, fetch role and set isAdmin
        this.authService.getCurrentUserRole().subscribe(role => {
          this.isAdmin = role === 'admin';
          console.log('User role:', role); // Check if the role is set correctly
        });

        // Load data after successful login
        this.loadCars();
        this.fetchCarOwners();
      },
      (error) => {
        this.errorMessage = "Invalid username or password.";
        console.error("Login error:", error);
      }
    );
  }

  onRegister(): void {
    const registerData = {
      username: this.registerUsername,
      password: this.registerPassword,
      email: this.registerEmail,
      role:this.role,
    };

    this.authService.register(registerData).subscribe(
      (response) => {
        this.authService.saveToken(response.token); // Save token
        this.isLoggedIn = true; // Update login status

        // Load data after successful registration
        this.loadCars();
        this.fetchCarOwners();
      },
      (error) => {
        this.errorMessage = "Registration failed. Please try again.";
        console.error("Registration error:", error);
      }
    );
  }


  logout(): void {
    this.authService.logout(); // Call the logout method in AuthService
    this.isLoggedIn = false;
    this.username = "";
    this.password = "";
    this.role="";
    this.errorMessage = null;
    this.sidebarVisible=false;
    this.registerUsername = "";
    this.registerPassword = "";
    this.registerEmail = "";
    this.role="";

    // Optionally, navigate to login page or show login form again
  }
  saveToken(token: string): void {
    localStorage.setItem('authToken', token);
  }


  loadCars(): void {
    this.carService.getCars().subscribe(
      (data: Car[]) => {
        this.cars = data.filter(car => car.properties && car.properties.carType && car.properties.carType.brandName);
        this.filteredCars = [...this.cars];
        },
      error => {
        console.error('Error fetching cars:', error);
      }
    );
  }
  fetchCarOwners(): void {
    this.carService.getOwners().subscribe(
      (owners) => {
        this.carOwners = owners.filter(owner=>owner.properties&&owner.properties.name);
        this.filteredOwners=[...this.carOwners];
      },
      (error) => {
        console.error('Error fetching car owners:', error);
      }
    );
  }
  applyFilters(): void {

    this.filteredCars = this.cars.filter(car => {
      const matchesBrand = this.brandNameFilter ? car.properties.carType.brandName.toLowerCase().includes(this.brandNameFilter.toLowerCase()) : true;
      const matchesColor = this.colorFilter ? car.properties.carType.color.toLowerCase().includes(this.colorFilter.toLowerCase()) : true;
      const matchesFuelType = this.fuelTypeFilter ? car.properties.carType.fuelType.toLowerCase().includes(this.fuelTypeFilter.toLowerCase()) : true;
      const matchesTypeName = this.typeNameFilter ? car.properties.carType.typeName.toLowerCase().includes(this.typeNameFilter.toLowerCase()) : true;
      const matchesPlateNumber = this.plateNumberFilter ? car.properties.plateNumber.toLowerCase().includes(this.plateNumberFilter.toLowerCase()) : true;
      const matchesMaxSpeed = car.properties.carType.maxSpeed >= this.maxSpeedFilter;
      const matchesPrice = car.properties.carType.price >= this.priceFilter;
      this.minFactoryDate = this.minFactoryDate ? new Date(this.minFactoryDate) : null;
      const factoryDate = new Date(car.properties.factoryDate); // Convert factory date to Date object
      const matchesMinDate = this.minFactoryDate ? factoryDate >= this.minFactoryDate : true;
      const matchesMaxDate = this.maxFactoryDate ? factoryDate <= this.maxFactoryDate : true; // Compare dates

      return matchesBrand && matchesColor && matchesFuelType && matchesTypeName && matchesPlateNumber &&
        matchesMaxSpeed && matchesPrice && matchesMinDate && matchesMaxDate;
    });
  }
  applyOwnerFilters():void{
    this.filteredOwners=this.carOwners.filter(CarOwner=>{
      const matchesName=this.NameFilter ? CarOwner.properties.name.toLowerCase().includes(this.NameFilter.toLowerCase()):true;
      const matchesEmail=this.EmailFilter ? CarOwner.properties.email.toLowerCase().includes(this.EmailFilter.toLowerCase()):true;
      const matchesPlateNumber = this.PlateNumberFilterOwner ? CarOwner.properties.plateNumber.toLowerCase().includes(this.PlateNumberFilterOwner.toLowerCase()) : true;
      const matchesContactNumber = this.ContactNumberFilter ? CarOwner.properties.contactNumber.toLowerCase().includes(this.ContactNumberFilter.toLowerCase()) : true;
return matchesName&&matchesEmail&&matchesContactNumber&&matchesPlateNumber;
    });
  }

  addCar(): void {
    this.carService.addCar(this.newCar).subscribe(
      (response) => {
        console.log('Car added successfully:', response);
        this.loadCars();
        this.newCar=this.resetNewCar();
        this.showDialog()
      },
      error => {
        console.error('Error adding car:', error);
      }
    );
  }
  addOwner():void{
    this.carService.addOwner(this.newOwner).subscribe(
      (response)=>{
        console.log('Owner added successfully:',response)
        this.fetchCarOwners()
        this.newOwner=this.resetNewOwner()
        this.showDialog()
      },
      error => {
        console.error('Error adding car:', error);
      }
    );
  }







  removeCar(car: Car): void {
    this.carService.removeCar(car).subscribe(
      (response) => {
        console.log('Car removed successfully:', response);
        this.loadCars();
      },
      error => {
        console.error('Error removing car:', error);
      }
    );
  }
  removeOwner(owner: CarOwner): void {
    this.carService.removeOwner(owner).subscribe(
      (response)=>{
        console.log('Owner removed successfully:',response);
        this.fetchCarOwners()
      },
      error => {
        console.error('Error removing owner',error);
      }
    );
   }

  resetNewCar(): Car {
    return {
      properties: {
        carType: {
          brandName: '',
          color: '',
          fuelType: '',
          maxSpeed: 0,
          price: 0,
          typeName: ''
        },
        factoryDate: '',
        plateNumber: ''
      }
    };
  }
  resetNewOwner():CarOwner{
    return {
      properties: {
        name:'',
        plateNumber: '',
        contactNumber:'',
        email:''
      }
    }
  }

  protected readonly name = name;

}
