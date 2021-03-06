import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { SERVER_API_URL } from '../../app.constants';

import { Kingdom } from './kingdom.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class KingdomService {

    private resourceUrl = SERVER_API_URL + 'api/kingdoms';

    constructor(private http: Http) { }

    create(kingdom: Kingdom): Observable<Kingdom> {
        const copy = this.convert(kingdom);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            return this.convertItemFromServer(jsonResponse);
        });
    }

    update(kingdom: Kingdom): Observable<Kingdom> {
        const copy = this.convert(kingdom);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            return this.convertItemFromServer(jsonResponse);
        });
    }

    find(id: number): Observable<Kingdom> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            return this.convertItemFromServer(jsonResponse);
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        const result = [];
        for (let i = 0; i < jsonResponse.length; i++) {
            result.push(this.convertItemFromServer(jsonResponse[i]));
        }
        return new ResponseWrapper(res.headers, result, res.status);
    }

    /**
     * Convert a returned JSON object to Kingdom.
     */
    private convertItemFromServer(json: any): Kingdom {
        const entity: Kingdom = Object.assign(new Kingdom(), json);
        return entity;
    }

    /**
     * Convert a Kingdom to a JSON which can be sent to the server.
     */
    private convert(kingdom: Kingdom): Kingdom {
        const copy: Kingdom = Object.assign({}, kingdom);
        return copy;
    }
}
