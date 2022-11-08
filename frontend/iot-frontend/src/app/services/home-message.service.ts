import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class HomeMessageService {
  constructor(private http: HttpClient) {}
  HOME_NAME = "home1";
  sendMessage(deviceName: string, message: string) {
    console.log(`sending request to`);
    return this.http
      .post(
        `http://localhost:8080/home/${this.HOME_NAME}/${deviceName}/${message}`,
        {}
      )
      .subscribe((data) => {
        console.log(data);
      });
  }
}
