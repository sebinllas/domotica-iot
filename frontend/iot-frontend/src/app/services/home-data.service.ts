import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class HomeDataService {
  constructor(private http: HttpClient) {
    this.HOME_NAME = "home1";
  }

  HOME_NAME = "home1";

  getTemperatureData() {
    return this.http
      .get(`http://localhost:8080/home/temp/${this.HOME_NAME}`);
  }

  getLightData() {
    return this.http
      .get(`http://localhost:8080/home/light/${this.HOME_NAME}`)
      .subscribe((data) => {
        console.log(data[0]);
      });
  }
}
