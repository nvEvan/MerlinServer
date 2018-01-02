import { Injectable } from "@angular/core";
import { Http } from "@angular/http";
import { UserPrivateData } from "../../models/composite/user-private-data.composite";
import { Observable } from "rxjs/Observable";
import { Response } from "@angular/http/src/static_response";
import { environment } from '../../../environments/environment'
/**
 * Service class used for User registration.
 * @author Alex
 */
@Injectable()
export class RegistrationService {

    constructor(private http: Http) { }

    /**
     * Register the apprentice
     * @param Apprentice 
     */
    registerApprentice(apprenticeData: UserPrivateData) {
       debugger; this.http.post("http://localhost:8085/merlinserver/rest/register/create", apprenticeData)
            .subscribe(
            data => {

            }, err => {

            }
            );
    }

    /**
     * Register the adept
     * @param adeptData 
     */
    registerAdept(adeptData: UserPrivateData, formData: FormData) {
        var self = this;

        this.http.post("http://localhost:8085/merlinserver/rest/register/create", adeptData).subscribe(
            data => {
                self.uploadCertificate(adeptData.user.username, formData);
            }, err => {
            }
            );
    }

    /**
     * Check the user registering is using a new username
     */
    isUniqueUsername(username: string): Observable<boolean> {
        let url: string = environment.url + "register/unique/" + username
        return this.http.get(url).map((response: Response) => response.json());
    }

    uploadCertificate(username: string, formData: FormData) {
        this.http.post(environment.url + "register/upload/" + username, formData)
        .subscribe(
            data => {

            }, err => {

            }
        );
    }
}