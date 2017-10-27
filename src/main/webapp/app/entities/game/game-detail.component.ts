import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { Game } from './game.model';
import { GameService } from './game.service';

@Component({
    selector: 'jhi-game-detail',
    templateUrl: './game-detail.component.html'
})
export class GameDetailComponent implements OnInit, OnDestroy {

    game: Game;
    private subscription: Subscription;
    private eventSubscriber: Subscription;
    private discussionDestinations = {};

    constructor(
        private eventManager: JhiEventManager,
        private gameService: GameService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInGames();
    }

    load(id) {
        this.gameService.find(id).subscribe((game) => {
            this.game = game;
            this.game.players.forEach((p)=> {
                this.discussionDestinations[p.userLogin] = true;
            });
        });
    }
    previousState() {
        window.history.back();
    }
    
    sendToggle(playerName: string) {
        this.discussionDestinations[playerName] = !this.discussionDestinations[playerName];
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInGames() {
        this.eventSubscriber = this.eventManager.subscribe(
            'gameListModification',
            (response) => this.load(this.game.id)
        );
    }
}
