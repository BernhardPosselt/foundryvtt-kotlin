import {isFirstGm, rollModeChoices} from './utils';
import {getBooleanSetting, setSetting} from './settings';
import {showKingdom} from './kingdom/sheet';
import {getKingdom} from './kingdom/storage';
import {addOngoingEvent, changeDegree, parseUpgradeMeta, reRoll} from './kingdom/rolls';
import {kingdomChatButtons} from './kingdom/chat-buttons';
import {StringDegreeOfSuccess} from './degree-of-success';
import {openCampingSheet} from './camping/sheet';
import {bindCampingChatEventListeners} from './camping/chat';
import {getDiffListeners} from './camping/effect-syncing';
import {getCamping, getCampingActor} from './camping/storage';
import {addDiscoverSpecialMealResult, addHuntAndGatherResult} from './camping/eating';
import {getActorByUuid} from './camping/actor';
import {migrate} from './migrations';
import {updateKingdomArmyConsumption} from './armies/utils';
import {openJournal} from './foundry-utils';
import {structureTokenMappingDialog} from './kingdom/dialogs/structure-token-mapping-dialog';
import {showStructureHints} from './kingdom/structures';


Hooks.on('ready', async () => {
    if (game instanceof Game) {
        const gameInstance = game;
        gameInstance.pf2eKingmakerTools.macros.structureTokenMappingMacro = structureTokenMappingDialog.bind(null, game);
        gameInstance.pf2eKingmakerTools.macros.viewKingdomMacro = showKingdom.bind(null, game);
        /* eslint-disable @typescript-eslint/no-explicit-any */
        gameInstance.pf2eKingmakerTools.macros.openCampingSheet = (): void => openCampingSheet(gameInstance);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'showManual', {
            name: 'Show Manual',
            scope: 'world',
            config: false,
            default: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register<string, string, []>('pf2e-kingmaker-tools', 'forceEnabledCompanionLeadershipBenefits', {
            name: 'Force Enabled Companion Leadership Benefits',
            scope: 'world',
            config: false,
            default: [],
            type: Array,
        });
        gameInstance.settings.register<string, string, number>('pf2e-kingmaker-tools', 'rpToXpConversionRate', {
            default: 1,
            config: false,
            type: Number,
            scope: 'world',
        });
        gameInstance.settings.register<string, string, number>('pf2e-kingmaker-tools', 'rpToXpConversionLimit', {
            default: 120,
            config: false,
            type: Number,
            scope: 'world',
        });
        gameInstance.settings.register<string, string, number>('pf2e-kingmaker-tools', 'xpPerClaimedHex', {
            default: 10,
            config: false,
            type: Number,
            scope: 'world',
        });
        gameInstance.settings.register<string, string, boolean>('pf2e-kingmaker-tools', 'cultOfTheBloomEvents', {
            default: true,
            config: false,
            type: Boolean,
            scope: 'world',
        });
        gameInstance.settings.register<string, string, boolean>('pf2e-kingmaker-tools', 'autoCalculateSettlementLevel', {
            default: true,
            config: false,
            type: Boolean,
            scope: 'world',
        });
        gameInstance.settings.register('pf2e-kingmaker-tools', 'proxyEncounterTable', {
            name: 'Proxy Random Encounter Table',
            hint: 'Name of the in world roll table that is rolled first to check what kind of encounter is rolled. Use the string "Creature" to roll on the region roll table in the proxy roll table or link another roll table of your choice. Leave blank to always roll on the region random encounter tables.',
            scope: 'world',
            config: false,
            default: '',
            type: String,
        });
        gameInstance.settings.register<string, string, string>('pf2e-kingmaker-tools', 'randomEncounterRollMode', {
            name: 'Random Encounter Roll Mode',
            scope: 'world',
            config: false,
            default: 'gmroll',
            type: String,
            choices: rollModeChoices,
        });
        gameInstance.settings.register<string, string, string>('pf2e-kingmaker-tools', 'kingdomEventRollMode', {
            name: 'Kingdom Events Roll Mode',
            scope: 'world',
            config: false,
            default: 'gmroll',
            type: String,
            choices: rollModeChoices,
        });
        gameInstance.settings.register('pf2e-kingmaker-tools', 'vanceAndKerensharaXP', {
            name: 'Enable Vance and Kerenshara XP rules',
            hint: 'Adds additional Milestone Events, more XP for claiming hexes and RP',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'capitalInvestmentInCapital', {
            name: 'Enable Capital Investment without Bank in Capital',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'reduceDCToBuildLumberStructures', {
            name: 'Reduce DC to Build Lumber Structures',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'automateResources', {
            name: 'Automatically sum worksites and farmlands',
            scope: 'world',
            config: false,
            default: 'kingmaker',
            requiresReload: true,
            type: String,
            choices: {
                kingmaker: 'Kingmaker',
                tileBased: 'Tile Based',
                manual: 'Manual',
            },
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'kingdomAlwaysAddHalfLevel', {
            name: 'Always add half Level to Skill',
            hint: 'If enabled, always adds half of the kingdom\'s level to a skill, even if it is untrained',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'kingdomAlwaysAddLevel', {
            name: 'Always add Level to Skill',
            hint: 'If enabled, always adds the kingdom\'s level to a skill, even if it is untrained. Overrides Always add half Level to Skill',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'kingdomSkillIncreaseEveryLevel', {
            name: 'Double Skill Increases',
            hint: 'If enabled, adds Skill Increases for all even levels from level 2 onwards',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'kingdomAllStructureItemBonusesStack', {
            name: 'All Structure Item Bonuses Stack',
            hint: 'If enabled, groups item bonuses from all structures, regardless of if they are same building type',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'kingdomIgnoreSkillRequirements', {
            name: 'Ignore Skill Proficiency Requirements for Kingdom Activities',
            scope: 'world',
            config: false,
            default: false,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register('pf2e-kingmaker-tools', 'autoCalculateArmyConsumption', {
            name: 'Automatically Calculate Army Consumption',
            hint: 'If enabled, gets all visible army tokens on all scenes and sums up their consumption',
            scope: 'world',
            config: false,
            default: true,
            requiresReload: true,
            type: Boolean,
        } as any);
        gameInstance.settings.register<string, string, number>('pf2e-kingmaker-tools', 'schemaVersion', {
            name: 'Schema Version',
            default: 1,
            config: false,
            type: Number,
            scope: 'world',
        });
        gameInstance.settings.register<string, string, string>('pf2e-kingmaker-tools', 'latestMigrationBackup', {
            name: 'Schema Version',
            default: '',
            config: false,
            type: String,
            scope: 'world',
        });

        // migrations
        await migrate(game, getKingdomSheetActor(game), getCampingActor(game));

        // hooks
        // army consumption
        const updateConsumption = async (actor: Actor | null): Promise<void> => {
            await updateKingdomArmyConsumption({
                actor,
                kingdomActor: getKingdomSheetActor(gameInstance),
                game: gameInstance,
            });
        };
        const forceUpdateConsumption = async (): Promise<void> => {
            await updateKingdomArmyConsumption({
                forceUpdate: true,
                kingdomActor: getKingdomSheetActor(gameInstance),
                game: gameInstance,
            });
        };
        Hooks.on('createToken', (token: StoredDocument<Token>) => {
            showStructureHints(game, token.actor);
            updateConsumption(token.actor);
        });
        Hooks.on('updateToken', (token: StoredDocument<Token>) => updateConsumption(token.actor));
        Hooks.on('deleteToken', (token: StoredDocument<Token>) => updateConsumption(token.actor));
        Hooks.on('updateActor', (actor: StoredDocument<Actor>) => updateConsumption(actor));
        Hooks.on('updateItem', (item: StoredDocument<Item>) => updateConsumption(item.actor));
        Hooks.on('createItem', (item: StoredDocument<Item>) => updateConsumption(item.actor));
        Hooks.on('deleteItem', (item: StoredDocument<Item>) => updateConsumption(item.actor));
        Hooks.on('deleteScene', () => forceUpdateConsumption());
        checkKingdomErrors(gameInstance);
        checkCampingErrors(gameInstance);

        // listen for camping sheet open
        const listeners = getDiffListeners(gameInstance);
        gameInstance.socket!.on('module.pf2e-kingmaker-tools', async (data: { action: string, data: any }) => {
            if (data.action === 'openCampingSheet') {
                openCampingSheet(gameInstance);
            } else if (data.action === 'openKingdomSheet') {
                await showKingdom(gameInstance);
            } else if (data.action === 'addDiscoverSpecialMealResult' && isFirstGm(gameInstance)) {
                const recipe = data.data.recipe as string | null;
                const criticalFailUuids = data.data.critFailUuids as string[];
                const actor = await getActorByUuid(data.data.actorUuid);
                const {specialIngredients, basicIngredients} = data.data;
                if (actor) {
                    const actorAndIngredients = {actor, specialIngredients, basicIngredients};
                    await addDiscoverSpecialMealResult(gameInstance, actorAndIngredients, recipe, criticalFailUuids);
                }
            } else if (data.action === 'addHuntAndGatherResult' && isFirstGm(gameInstance)) {
                const actor = await getActorByUuid(data.data.actorUuid);
                const {specialIngredients, basicIngredients} = data.data;
                if (actor) {
                    await addHuntAndGatherResult(gameInstance, {
                        actor,
                        basicIngredients,
                        specialIngredients,
                    });
                }
            } else {
                // players changed data and GM needs to sync effects
                const sheetActor = getCampingActor(gameInstance);
                if (sheetActor && isFirstGm(gameInstance)) {
                    const camping = getCamping(sheetActor);
                    for (const listener of listeners) {
                        if (listener.canHandle(data.action)) {
                            listener.onReceive(camping);
                        }
                    }
                }
            }
        });

        if (isFirstGm(gameInstance) && getBooleanSetting(gameInstance, 'showManual')) {
            await setSetting(gameInstance, 'showManual', false);
            await openJournal('Compendium.pf2e-kingmaker-tools.kingmaker-tools-journals.JournalEntry.iAQCUYEAq4Dy8uCY');
        }
    }
});

Hooks.on('init', async () => {
    await loadTemplates([
        'modules/pf2e-kingmaker-tools/templates/common/skills.partial.hbs',
        'modules/pf2e-kingmaker-tools/templates/camping/activity.partial.hbs',
        'modules/pf2e-kingmaker-tools/templates/camping/eating.partial.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/structure-browser-item.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/sidebar.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/status.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/skills.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/turn.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/groups.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/feats.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/notes.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/settlements.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/settlement.hbs',
        'modules/pf2e-kingmaker-tools/templates/kingdom/effects.hbs',
    ]);
    // load custom token mappings if kingmaker module isn't installed
    if (game instanceof Game) {
        if (!game.modules.get('pf2e-kingmaker')?.active) {
            game.modules.get('pf2e-kingmaker-tools')
                ?.updateSource({flags: {'pf2e-kingmaker-tools': {'pf2e-art': 'modules/pf2e-kingmaker-tools/token-map.json'}}});
        }
    }
});

Hooks.on('renderChatLog', () => {
    if (game instanceof Game) {
        const gameInstance = game;
        const chatLog = $('#chat-log');
        for (const button of kingdomChatButtons) {
            chatLog.on('click', button.selector, (event: Event) => {
                const actor = getKingdomSheetActor(gameInstance);
                if (actor) {
                    return button.callback(gameInstance, actor, event);
                }
            });
        }
        bindCampingChatEventListeners(gameInstance);
    }
});

type LogEntry = {
    name: string,
    icon?: string,
    condition?: (html: JQuery) => boolean,
    callback: (html: JQuery) => void,
};

function getKingdomSheetActor(game: Game): Actor | undefined {
    return game?.actors?.find(a => a.name === 'Kingdom Sheet');
}

function ifKingdomActorExists(game: Game, el: HTMLElement, callback: (actor: Actor) => void): void {
    // TODO: replace this with a data-actor-id lookup in the el
    const actor = getKingdomSheetActor(game);
    if (actor) {
        callback(actor);
    } else {
        ui.notifications?.error('Could not find actor with name Kingdom Sheet');
    }
}

Hooks.on('getChatLogEntryContext', (html: HTMLElement, items: LogEntry[]) => {
    if (game instanceof Game) {
        const gameInstance = game;
        const hasActor = (): boolean => getKingdomSheetActor(gameInstance) !== undefined;
        const hasContentLink = (el: JQuery): boolean => hasActor() && el[0].querySelector('.content-link') !== null;
        const hasMeta = (el: JQuery): boolean => hasActor() && el[0].querySelector('.km-roll-meta') !== null;
        const canChangeDegree = (chatMessage: HTMLElement, direction: 'upgrade' | 'downgrade'): boolean => {
            const cantChangeDegreeUnless: StringDegreeOfSuccess = direction === 'upgrade' ? 'criticalSuccess' : 'criticalFailure';
            const meta = chatMessage.querySelector('.km-upgrade-result');
            return hasActor() && meta !== null && parseUpgradeMeta(chatMessage).degree !== cantChangeDegreeUnless;
        };
        const canReRollUsingFame = (el: JQuery): boolean => {
            const actor = getKingdomSheetActor(gameInstance);
            return actor ? getKingdom(actor).fame.now > 0 && hasMeta(el) : false;
        };
        const canReRollUsingCreativeSolution = (el: JQuery): boolean => {
            const actor = getKingdomSheetActor(gameInstance);
            return actor ? getKingdom(actor).creativeSolutions > 0 && hasMeta(el) : false;
        };
        const canReRollUsingSupernaturalSolution = (el: JQuery): boolean => {
            const actor = getKingdomSheetActor(gameInstance);
            return actor ? getKingdom(actor).supernaturalSolutions > 0 && hasMeta(el) : false;
        };
        items.push({
            name: 'Re-Roll Using Fame/Infamy',
            icon: '<i class="fa-solid fa-dice-d20"></i>',
            condition: canReRollUsingFame,
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => reRoll(actor, el[0], 'fame')),
        }, {
            name: 'Re-Roll Using Creative Solution',
            icon: '<i class="fa-solid fa-dice-d20"></i>',
            condition: canReRollUsingCreativeSolution,
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => reRoll(actor, el[0], 'creative-solution')),
        }, {
            name: 'Re-Roll Using Supernatural Solution',
            icon: '<i class="fa-solid fa-dice-d20"></i>',
            condition: canReRollUsingSupernaturalSolution,
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => reRoll(actor, el[0], 'supernatural-solution')),
        }, {
            name: 'Re-Roll',
            condition: hasMeta,
            icon: '<i class="fa-solid fa-dice-d20"></i>',
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => reRoll(actor, el[0], 're-roll')),
        }, {
            name: 'Re-Roll Keep Higher',
            condition: hasMeta,
            icon: '<i class="fa-solid fa-dice-d20"></i>',
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => reRoll(actor, el[0], 'keep-higher')),
        }, {
            name: 'Re-Roll Keep Lower',
            condition: hasMeta,
            icon: '<i class="fa-solid fa-dice-d20"></i>',
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => reRoll(actor, el[0], 'keep-lower')),
        }, {
            name: 'Upgrade Degree of Success',
            condition: (el: JQuery) => canChangeDegree(el[0], 'upgrade'),
            icon: '<i class="fa-solid fa-arrow-up"></i>',
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => changeDegree(actor, el[0], 'upgrade')),
        }, {
            name: 'Downgrade Degree of Success',
            condition: (el: JQuery) => canChangeDegree(el[0], 'downgrade'),
            icon: '<i class="fa-solid fa-arrow-down"></i>',
            callback: el => ifKingdomActorExists(gameInstance, el[0], (actor) => changeDegree(actor, el[0], 'downgrade')),
        }, {
            name: 'Add to Ongoing Events',
            icon: '<i class="fa-solid fa-plus"></i>',
            condition: hasContentLink,
            callback: async (el) => {
                ifKingdomActorExists(gameInstance, el[0], async (actor) => {
                    const link = el[0].querySelector('.content-link') as HTMLElement | null;
                    const uuid = link?.dataset?.uuid;
                    const text = link?.innerText;
                    if (uuid && text) {
                        await addOngoingEvent(actor, uuid, text);
                    }
                });
            },
        });
    }
});

function checkKingdomErrors(game: Game): void {
    const actor = getKingdomSheetActor(game);
    if (actor && !actor.getFlag('pf2e-kingmaker-tools', 'kingdom-sheet')) {
        ui.notifications?.error('Found an Actor with name "Kingdom Sheet" that has not been imported using the "View Kingdom" Macro! Please delete the actor and re-import it.');
    }
}

function checkCampingErrors(game: Game): void {
    const actor = getCampingActor(game);
    if (actor && !actor.getFlag('pf2e-kingmaker-tools', 'camping-sheet')) {
        ui.notifications?.error('Found an Actor with name "Camping Sheet" that has not been imported using the "Camping" Macro! Please delete the actor and re-import it.');
    }
}