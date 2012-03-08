var $micro = {};
(function () {
  var _constants = $micro.constants = {};

  _constants.cardEnum = {period: 0, event: 1, scene: 2};
  _constants.reverseCardEnum = ['period', 'event', 'scene'];
  _constants.top = 40;
  _constants.left = 40;
  _constants.spacing = 20;
  _constants.periodContainerSize = {width: 200, height: 220};
  _constants.eventContainerSize = {width: 200, height: 200};
  _constants.sceneContainerSize = {width: 160, height: 200};
  // Game id initialize on first update state.

  var _relation = _constants.relation = {};
  _relation.game = {child: 'period'};
  _relation.period = {parent: 'game', child: 'event'};
  _relation.event = {parent: 'period', child: 'scene'};
  _relation.scene = {parent: 'event'};
})();
(function () {
  var legacyMode = false;
  var plusesShown = false;

  var _support = $micro.support = {};

  _support.hidePluses = function () {
    $('.period_plus_container').hide();
    $('.period_plus_event_container').hide();
    $('.event_plus_container').hide();
    $('.event_plus_scene_container').hide();
    $('.scene_plus_container').hide();
    plusesShown = false;
  };

  _support.showPluses = function () {
    plusesShown = true;

    if (!legacyMode) {
      $('.period_plus_container').show();
    }
    if ($micro.turn.isAction('playBookend')) {
      return;
    }

    $('.period_plus_event_container').show();
    $('.event_plus_container').show();
    if ($micro.turn.isAction('round0')) {
      return;
    }

    $('.event_plus_scene_container').show();
    $('.scene_plus_container').show();
  };

  // TODO: this also has to enforce that a scene must be a dictated scene
  _support.setLegacyMode = function (mode) {
    if (mode == legacyMode) {
      return;
    }
    legacyMode = mode;

    if (mode) {
      $('.period_plus_container').hide();
    } else {
      if (plusesShown) {
        _support.showPluses();
      }
    }
  };

  var _getPlayerId = _support.getPlayerId = function () {
    return $('#playerLabel').data('id');
  };

  _support.getPlayerQuery = function () {
    var id = _getPlayerId();
    var retval = $();
    var players = $('.player');
    for (var i = 0; i < players.length; i++) {
      var playerQuery = $(players[i]);
      if (playerQuery.data('id') == id) {
        retval = retval.add(playerQuery);
      }
    }
    return retval;
  };
})();
$(document).ready(function () {
  var i;
  var _turn = $micro.turn = {};
  var firstRound = true;

  // Effectively a 2 way dictionary for the gameAction enum to a more readable string.
  var gameActionString = ['addPlayers', 'bigPicture', 'playBookend', 'palette',
    'round0',
    'pickFocus', 'play', 'playNested', 'pickLegacy', 'playLegacy'];
  var gameActionIndex = {};
  for (i = 0; i < gameActionString.length;
      i++) {
    gameActionIndex[gameActionString[i]] = i;
  }

  var _myTurn = false;
  var _actionDone = false;
  var numPlayers = -1;
  var currAction = -1;
  var currLensIndex = -1;
  var currPlayerIndex = -1;
  var currPlayerQuery = -1;
  var players = [];
  _turn.setCurrPlayer = function (round, turn, playerObjs, legacies) {
    var nextAction = turn;
    var nextLensIndex = 0;
    var nextPlayerIndex = 0;
    numPlayers = playerObjs.length;

    // Defaults only apply in round 0
    if (round != 0) {
      firstRound = false;
      nextLensIndex = (round - 1) % numPlayers;
      nextAction = gameActionIndex.play;
      if (turn == 0) {
        nextAction = gameActionIndex.pickFocus;
      } else if (turn == 2 || turn == numPlayers
          + 3) {
        nextAction = gameActionIndex.playNested;
      } else if (turn == numPlayers + 4) {
        nextAction = gameActionIndex.pickLegacy;
      } else if (turn == numPlayers + 5) {
        nextAction = gameActionIndex.playLegacy;
      }
      if (turn > numPlayers + 3) {
        nextPlayerIndex = numPlayers + 1;
      } else if (turn > numPlayers + 1) {
        nextPlayerIndex = numPlayers;
      } else if (turn > 2) {
        nextPlayerIndex = turn - 2;
      }
    }
    //If nothings changed, don't do anything
    if (nextLensIndex == currLensIndex && nextAction == currAction
        && nextPlayerIndex == currPlayerIndex) {
      if ($micro.legacy.auctionChanged(legacies)) {
        return true;
      }
      // These may have their own functions eventually, much like auction changed;
      if (round == 0 && (turn == gameActionIndex.palette || turn
              == gameActionIndex.round0)) {
        return true;
      }

      return false;
    }
    if (nextLensIndex != currLensIndex) {
      $('#lens').find('span').text(
          playerObjs[nextLensIndex].username);
    }

    currAction = nextAction;
    currLensIndex = nextLensIndex;
    currPlayerIndex = nextPlayerIndex;

    $('.player').remove();
    var legacyIndex = numPlayers + 1;
    for (i = 0; i <= legacyIndex; i++) {
      var index = currLensIndex + i;
      if (i == legacyIndex) {
        index = currLensIndex + numPlayers - 1;
      }
      index = index % numPlayers;
      var player = playerObjs[index];
      var playerDiv = $('<div></div>');
      playerDiv.text(player.username);
      playerDiv.data('id', player.userID);

      var legacy = '';
      var maxRound = -1;
      for (var j = 0; j < legacies.length; j++) {
        var currLegacy = legacies[j];
        if (currLegacy.playerID != player.userID || currLegacy.round
            < maxRound) {
          continue;
        }
        legacy = currLegacy.legacy;
        maxRound = currLegacy.round;
      }
      playerDiv.data('legacy', legacy);

      playerDiv.addClass('player');
      if (i == currPlayerIndex) {
        playerDiv.addClass('bold');
        currPlayerQuery = playerDiv;
      }
      $micro.header.initPlayer(playerDiv);
      $('#centerHeader').append(playerDiv);
    }
    players = $('.player');
    return true;
  };

  // TODO: more in here to support debug more later
  _turn.setPlayer = function (id, name) {
    $('#playerLabel').data('id', id);
    $('#playerLabel').find('span').text(name);
  };

  var _isAction = _turn.isAction = function (action) {
    return gameActionIndex[action] == currAction;
  };

  // Id is id of the card added. Needed for nested play.
  _turn.progress = function (id) {
    if (firstRound) {
      if (currAction == gameActionIndex['bigPicture']) {
        currAction = gameActionIndex['playBookend'];
        startBookends();
      } else if (currAction == gameActionIndex['playBookend']) {
        currAction = gameActionIndex['palette'];
        startPalette();
      } else if (currAction == gameActionIndex['palette']) {
        currAction = gameActionIndex['round0'];
        startRound0();
      } else if (currAction == gameActionIndex['round0']) {
        currAction = gameActionIndex['pickFocus'];
        startPickFocus();
      }
      return;
    }

    if (currPlayerIndex == 0) {
      if (currAction == gameActionIndex['pickFocus']) {
        currAction = gameActionIndex['play'];
        startPlay();
      } else if (currAction == gameActionIndex['play']) {
        currAction = gameActionIndex['playNested'];
        startNestedPlay(id);
      } else {
        playerProgress();
        currAction = gameActionIndex['play'];
        startPlay();
      }
      return;
    }

    // Deal with the easy case first
    if (currPlayerIndex > 0 && currPlayerIndex < numPlayers) {
      playerProgress();

      startPlay();
      return;
    }

    // If lenses second turn
    if (currPlayerIndex == numPlayers) {
      if (currAction == gameActionIndex['play']) {
        currAction = gameActionIndex['playNested'];
        startNestedPlay(id);
      } else {
        playerProgress();
        currAction = gameActionIndex['pickLegacy'];
        $micro.legacy.startPick();
      }
      return;
    }

    // If legacy turn
    if (currPlayerIndex == numPlayers + 1) {
      if (currAction == gameActionIndex['pickLegacy']) {
        currAction = gameActionIndex['playLegacy'];
        $micro.legacy.startPlay();
      } else {
        $micro.support.setLegacyMode(false);
        playerProgress();
        currAction = gameActionIndex['pickFocus'];
        startPickFocus();
      }
    }
  };

  var playerProgress = function () {
    if (currPlayerIndex == numPlayers + 1) {
      currPlayerIndex = 0;

      // Reorder the players
      $(players[0]).insertAfter($(players[numPlayers + 1]));
      $(players[numPlayers + 1]).remove();
      $(players[1]).clone().insertAfter($(players[numPlayers]));

      players = $('.player');

      // Init the newly cloned player
      $micro.header.initPlayer($(players[numPlayers]));
    } else {
      currPlayerIndex++;
      currPlayerQuery.removeClass('bold');
    }

    currPlayerQuery = $(players[currPlayerIndex]);
    currPlayerQuery.addClass('bold');
    updateMyTurn();
  };

  var startPlay = function () {
    if (!_canPlay()) {
      //$micro.update.unpause();
      return;
    }
    $micro.support.showPluses();
  };

  var startNestedPlay = function (id) {
    if (!_canPlay()) {
      //$micro.update.unpause();
      return;
    }

    // If scene, force an empty play
    if (id[0] == 's') {
      // TODO: move this logic elsewhere
      $.post('PlayNestedCard', {
            gameID: $micro.constants.gameId,
            pass: true
          }, function (json) {
            if (json.success) {
              _turn.progress();
            } else {
              // TODO: Undo, or something...
              alert('Setting answer failed');
              // Just refresh for now
              location.reload();
            }
          }, 'json'
      ).error(function () {
        alert('Could not connect to server');
        location.reload();
      });
      return;
    }
    // Cancelation is the same as choosing not to play the nested card.
    $('#' + id + '_childPlusContainer').click();
  };

  var startPickFocus = function () {
    if (!_canPlay()) {
      //$micro.update.unpause();
      return;
    }
    $micro.support.hidePluses();
    $('#lightbox').click();
    $('#lens').children('span').text($(players[0]).text());
    $('#lens').show();
    $('#focus').hide();

    $('#focusButton').click(function () {
      $('#lightbox').show();
      $('#createFocus').show();
    });
    $('#createFocusDone').click(function () {
      $(this).hide();
      $(this).off('click');
      var text = $('#createFocusTextbox').val();

      $.post('SetFocus', {
            gameID: $micro.constants.gameId,
            focus: text
          }, function (json) {
            if (json.success) {
              $('#focusButton').off('click');
              $('#focusButton').hide();
              $('#focus').children('span').text(text);
              $('#focus').show();

              $('#lightbox').click();
              $micro.turn.progress();
            } else {
              alert('Choosing Focus Failed');
              // Just refresh for now
              location.reload();
            }
          }, 'json'
      ).error(function () {
        alert('Could not connect to server');
        location.reload();
      });
    });

    $('#focusButton').show();
  };

  var startBigPicture = function () {
    if (!_canPlay()) {
      //$micro.update.unpause();
      return;
    }

    $('#createBigPicture').show();
    $('#createBigPictureDone').click(function () {
      $(this).hide();
      $(this).off('click');
      var text = $('#createBigPictureText').val();
      $('title').text(text);
      $('#bigPicture').find('span').text(text);

      $.post('SetBigPicture', {
            gameID: $micro.constants.gameId,
            bigPicture: text
          }, function (json) {
            //if(json.success) {

            // Don't need the bigPicture anymore for now anyways.
            $('#createBigPictureTitle').remove();
            $('#lightbox').click();
            $micro.turn.progress();
            //} else {
            //	alert('Choosing Focus Failed');
            //	// Just refresh for now
            //	location.reload();
            //}
          }, 'json'
      ).error(function () {
        alert('Could not connect to server');
        location.reload();
      });
    });
  };

  var startBookends = function () {
    if (!_canPlay()) {
      //$micro.update.unpause();
      return;
    }

    var periods = $micro.period.count();
    if (periods != 0) {
      if (periods != 1) {
        alert(
            'no more than 1 period allowed when adding bookends!');
      }

      $micro.support.showPluses();
      return;
    }

    var loc = $micro.period.getLocationOfCard(0);

    var html = $($('#periodTemplate').html());
    html.attr('style', 'top : ' + loc.y + 'px;left : ' + loc.x + 'px;');
    html.data('index', 0);
    html.data('parentId', 'g' + $micro.constants.gameId);

    $('#game').append(html);
    html.find('.period_tone').click($micro.cards.toneEditable);

    $('.period_cancel').hide();
    $('.period_done').click(function () {
      $micro.cards.cardDone.apply(this, ['period']);
    });
  };

  var startPalette = function () {
    if (!_canPlay()) {
      //$micro.update.unpause();
      return;
    }

    //$micro.update.pause();
    $('#palette').show();

    var createPaletteClick = function (skip, suggest) {
      return function () {
        var params = {skip: skip};
        if (!skip) {
          params.in_game = suggest;
          params.text = $('#paletteText').val();
        }
        $.post('PlayPalette', params,
            function (json) {
              if (!json.success) {
                alert('Adding to palette failed');
                // Just refresh for now
                location.reload();
              } else {
                _turn.setActionDone(true);
              }
            });

        $('#paletteYes').hide().off('click');
        $('#paletteNo').hide().off('click');
        $('#paletteSkip').hide().off('click');
        $('#paletteText').hide().val('');
      };
    };

    $('#paletteYes').click(createPaletteClick(false, true));
    $('#paletteNo').click(createPaletteClick(false, false));
    $('#paletteSkip').click(createPaletteClick(true));

    $('#paletteYes').show();
    $('#paletteNo').show();
    $('#paletteSkip').show();
    $('#paletteText').show();
  };

  var startRound0 = function () {
    if (!_canPlay()) {
      return;
    }

    $('#palette').hide();
    $micro.support.showPluses();
  };

  _turn.init = function (lastCardId) {
    updateMyTurn();
    // Return if it is not your turn
    if (!_canPlay()) {
      return;
    }

    if (_isAction('play')) {
      startPlay();
    } else if (_isAction('playNested')) {
      startNestedPlay(lastCardId);
    } else if (_isAction('pickLegacy')) {
      $micro.legacy.startPick();
    } else if (_isAction('playLegacy')) {
      $micro.legacy.startPlay();
    } else if (_isAction('pickFocus')) {
      startPickFocus();
    }// Otherwise in first round
    else if (_isAction('bigPicture')) {
      startBigPicture();
    } else if (_isAction('playBookend')) {
      startBookends();
    } else if (_isAction('palette')) {
      startPalette();
    } else if (_isAction('round0')) {
      startRound0();
    }
  };

  var updateMyTurn = function () {
    var myTurn = $micro.support.getPlayerId() == $(
        $('.player')[currPlayerIndex]).data('id');
    if (_myTurn == myTurn) {
      return;
    }
    _myTurn = myTurn;
    _updatePause();
  };

  _turn.setActionDone = function (actionDone) {
    if (actionDone == _actionDone) {
      return;
    }
    _actionDone = actionDone;
    _updatePause();
  };

  var _updatePause = _turn.updatePause = function () {
    return;
    if (_canPlay() && !$micro.legacy.isAuction()) {
      $micro.update.pause();
    } else {
      $micro.update.unpause();
    }
  };

  var _canPlay = _turn.canPlay = function () {
    if ($micro.legacy.isAuction() || _isAction('palette') || _isAction(
            'round0')) {
      return !_actionDone;
    }
    return _myTurn;
  };
});

// Stuff to do with legacy
(function () {
  var _legacy = $micro.legacy = {};

  var _isAuction = false;
  var _freeLegacy = '';

  _legacy.auctionChanged = function (legacies) {
    var newIsAuction = false;
    var newFreeLegacy = '';
    var round = -1;

    for (var i = 0; i < legacies.length; i++) {
      var legacy = legacies[i];
      if (!legacy.playerID) {
        newIsAuction = true;
        newFreeLegacy = legacy.legacy;
        round = legacy = legacy.round;
        break;
      }
    }

    var change = _updateAuction(newIsAuction, newFreeLegacy);
    if (change) {
      var playerToLegacy = {};
      for (i = 0; i < legacies.length; i++) {
        legacy = legacies[i];
        if (legacy.round != round || !legacy.playerID) {
          continue;
        }

        playerToLegacy[legacy.playerID] = legacy.legacy;
      }

      var players = $('.player');
      for (i = 0; i < players.length; i++) {
        var player = $(players[i]);
        player.data('legacy', playerToLegacy[player.data('id')]);
      }
    }
    return change;
  };

  var _updateAuction = function (newIsAuction, newFreeLegacy) {
    // change if free legacy changed or you were not in an auction and are in one now.
    var change = (!_isAuction && newIsAuction) || _freeLegacy != newFreeLegacy;
    _isAuction = newIsAuction;
    _freeLegacy = newFreeLegacy;
    if (change) {
      $micro.turn.updatePause();
    }

    return change;
  };

  _legacy.isAuction = function () {
    return _isAuction;
  };

  _legacy.startPick = function () {
    if (!$micro.turn.canPlay()) {
      //$micro.update.unpause();
      return;
    }

    $micro.support.hidePluses();
    $('#lightbox').click();
    $('#lens').hide();
    $('#focus').hide();

    $('#legacyButton').click(function () {
      $('#lightbox').show();
      $('#legacyContainer').show();
    });

    var currLegacy = $micro.support.getPlayerQuery().data('legacy');
    if (currLegacy) {
      pickNew(currLegacy);
    } else {
      if (_isAuction) {
        alert(
            'WARNING: Starting create of legacy during auction');
      }
      startCreate();
    }

    $('#legacyButton').show();
  };

  var startCreate = function () {
    $('#legacyContainer').css({'margin-left': '-100px', 'width': '200px'});
    $('#oldLegacy').hide();

    $('#newLegacyText').hide();
    $('#newLegacyTextbox').val('');
    $('#newLegacyTextbox').show();
    $('#newLegacyDone').text('Done');

    $('#newLegacyDone').click(function () {
      $(this).off('click');
      var text = $('#newLegacyTextbox').val();
      $.post('SetLegacy', {
            gameID: $micro.constants.gameId,
            pass: false,
            legacy: text
          }, function (json) {
            if (json.success) {
              $micro.support.getPlayerQuery().data('legacy', text);

              $('#legacyButton').off('click');
              $('#legacyButton').hide();
              $('#legacyContainer').hide();

              $('#lightbox').click();
              $micro.turn.progress();
            } else {
              alert('Creating Legacy Failed');
              // Just refresh for now
              location.reload();
            }
          }, 'json'
      ).error(function () {
        alert('Could not connect to server');
        location.reload();
      });
    });
  };

  var pickNew = function (old) {
    $('#legacyContainer').css({'margin-left': '-210px', 'width': '420px'});

    $('#oldLegacy').show();
    $('#oldLegacyText').text(old);
    $('#oldLegacyKeep').click(function () {
      $(this).off('click');

      $.post('SetLegacy', {
            gameID: $micro.constants.gameId,
            pass: true
          }, function (json) {
            if (json.success) {
              $('#legacyButton').off('click');
              $('#legacyButton').hide();
              $('#legacyContainer').hide();
              $('#lightbox').click();
              if (!_isAuction) {
                $micro.turn.progress();
              }
            } else {
              alert('Creating Legacy Failed');
              // Just refresh for now
              location.reload();
            }
          }, 'json'
      ).error(function () {
        alert('Could not connect to server');
        location.reload();
      });
    });

    if (_isAuction) {
      $('#newLegacyTextbox').hide();
      $('#newLegacyText').text(_freeLegacy);
      $('#newLegacyText').show();
      $('#newLegacyDone').text('Pick');

      $('#newLegacyDone').click(function () {
        $(this).off('click');
        $.post('SetLegacy', {
              gameID: $micro.constants.gameId,
              pass: false
            }, function (json) {
              if (json.success) {
                $micro.support.getPlayerQuery().data('legacy', _freeLegacy);

                $('#legacyButton').off('click');
                $('#legacyButton').hide();
                $('#legacyContainer').hide();
                _freeLegacy = '';
                $micro.turn.setActionDone(true);
                $('#lightbox').click();
              } else {
                alert('Creating Legacy Failed');
                // Just refresh for now
                location.reload();
              }
            }, 'json'
        ).error(function () {
          alert('Could not connect to server');
          location.reload();
        });
      });
    } else {
      $('#newLegacyText').hide();
      $('#newLegacyTextbox').val('');
      $('#newLegacyTextbox').show();
      $('#newLegacyDone').text('Done');

      $('#newLegacyDone').click(function () {
        $(this).off('click');
        var text = $('#newLegacyTextbox').val();
        $.post('SetLegacy', {
              gameID: $micro.constants.gameId,
              pass: false,
              legacy: text
            }, function (json) {
              if (json.success) {
                var playerQuery = $micro.support.getPlayerQuery();
                var oldLegacy = playerQuery.data('legacy');
                playerQuery.data('legacy', text);

                $('#legacyButton').off('click');
                $('#legacyButton').hide();
                $('#legacyContainer').hide();
                $('#lightbox').click();

                if ($micro.support.getPlayerQuery().data('legacy')) {
                  _updateAuction(
                      true, oldLegacy);
                } else {
                  $micro.turn.progress();
                }
              } else {
                alert('Creating Legacy Failed');
                // Just refresh for now
                location.reload();
              }
            }, 'json'
        ).error(function () {
          alert('Could not connect to server');
          location.reload();
        });
      });
    }
  };

  _legacy.startPlay = function () {
    if (!$micro.turn.canPlay()) {
      //$micro.update.unpause();
      return;
    }
    $('#lens').hide();
    $('#focus').hide();
    $('#lightbox').click();
    $micro.support.setLegacyMode(true);
    $micro.support.showPluses();
  };
})();

// Stuff to do with header
(function () {
  var _header = $micro.header = {};

  _header.initPlayer = function (player) {
    player.hover(function () {
      var legacy = player.data('legacy');
      if (!legacy) {
        return;
      }
      $('#legacyText').children('span').text(legacy);
      $('#legacyText').show();
    }, function () {
      $('#legacyText').hide();
    });
  };
})();

(function () {
  var onResize = function () {
    var height = $(window).height() - $('#header').height();
    if (height < 0) {
      height = 0;
    }
    $('#game').attr('style', 'left : ' + window.scrollX + 'px;width : ' + $(
        window).width() + 'px; height : ' + height + 'px;');
  };

  $micro.getRootId = function (id) {
    return id.split('_')[0];
  };

  $(document).ready(function () {
    (function () {
      var cards = $micro.cards = {};

      var periods = [];
      var cardMap = {};

      // This should be able to be moved to a better place
      var toneEditable = cards.toneEditable = function () {
        tone = $(this).hasClass('dark');
        if (tone) {
          $(this).removeClass('dark');
        } else {
          $(this).addClass('dark');
        }
      };

      var startSceneDone = function (id) {
        var scene = $('#' + id);
        var done = scene.find('.scene_done');
        done.show();

        var tone = scene.find('.scene_tone');
        var answerTextbox = $(scene.find('.scene_textbox')[2]);
        var answer = $(scene.find('.scene_text')[2]);

        answer.hide();
        answerTextbox.show();

        tone.click(toneEditable);
        tone.show();

        done.click(function () {
          answer.text(answerTextbox.val());
          answerTextbox.hide();
          answerTextbox.val('');
          answer.show();

          done.off('click');
          done.hide();
          tone.off('click');

          $.post('SetSceneAnswer', {
                cardID: id.substring(1),
                tone: tone.hasClass('dark'),
                gameID: $micro.constants.gameId,
                answer: answer.text(),
              }, function (json) {
                if (json.success) {
                  var parent = cardMap[id].parent;
                  var counter = $('#' + parent + '_placeholder').find(
                      '.scenes_placeholder_number');
                  var count = Number(counter.text());
                  count++;
                  counter.text(count);
                  $micro.turn.progress(id);
                } else {
                  // TODO: Undo, or something...
                  alert('Setting answer failed');
                  // Just refresh for now
                  location.reload();
                }
              }, 'json'
          ).error(function () {
            alert('Could not connect to server');
            location.reload();
          });

          done.hide();
        });
      };

      // When the done button is pushed on a card.
      var cardDone = cards.cardDone = function (card) {
        var text = undefined;
        var setting = undefined;
        var isScene = card == 'scene';
        var textbox = $('#temp').find('.' + card + '_textbox');
        if (isScene) {
          text = $(textbox[0]).val();
          setting = $(textbox[1]).val();
        } else {
          text = textbox.val();
        }

        var position = $('#temp_container').data('index');
        var parent = $('#temp_container').data('parentId');
        var tone = $('#temp').find('.' + card + '_tone').hasClass('dark');

        var url;
        var requestParameters = {
          gameID: $micro.constants.gameId,
          tone: tone,
          text: text,
          description: ''
        };

        var isFirstCard = $micro.period.count() == 0;
        var isRound0 = $micro.turn.isAction('round0');
        if ($micro.turn.isAction('playNested')) {
          requestParameters.pass = false;
          url = 'PlayNestedCard';
        } else {
          requestParameters.pes = $micro.constants.cardEnum[card];
          requestParameters.position = position + 1;
          requestParameters.parentID = parent.substring(1);

          if ($micro.turn.isAction('play')) {
            url = 'PlayNewCard';
          } else if ($micro.turn.isAction('playBookend')) {
            url = 'PlayBookend';
          } else if (isRound0) {
            url = 'PlayFirstPass';
          } else {
            url = 'PlayLegacy';
          }
        }

        if (isScene) {
          requestParameters.dictated = true;
          requestParameters.scene = setting;
        }
        $.post(url,
            requestParameters, function (json) {
              if (!json.success) {
                // TODO: Undo
                alert('Your card was not able to be added to the database');
                // Just refresh for now
                location.reload();
              } else if (json.id) {
                var id = card.substring(0, 1) + json.id;
                var first = card != 'event' ? 'left' : 'top';
                var second = card != 'event' ? 'right' : 'bottom';
                $('#temp_container').attr('id', id + '_container');
                $('#temp').attr('id', id);
                $('#temp_' + first + 'PlusContainer').attr('id', id + '_'
                    + first + 'PlusContainer');
                $('#temp_' + second + 'PlusContainer').attr('id', id + '_'
                    + second + 'PlusContainer');
                $('#temp_childPlusContainer').attr('id', id
                    + '_childPlusContainer');

                $micro[card].insert(id, position, parent);

                if (isFirstCard) {
                  $('#' + id + '_container').find(
                      '.period_plus_container').show();
                } else if (isScene) {
                  startSceneDone(id);
                } else if (!isRound0) {
                  $micro.turn.progress(id);
                }
                $micro.update.unpause();
              } else {
                // This should not happen....
                alert('no id was received');
                location.reload();
              }
            }, 'json').error(function () {
          alert('Could not connect to server');
          location.reload();
        });

        $(this).hide();

        var cardText = $('#temp').find('.' + card + '_text');

        if (isScene) {
          $(cardText[0]).text(text);
          $(cardText[1]).text(setting);
          $(this).off('click');
          $(this).text('Done');
        } else {
          cardText.text(text);
        }
        textbox.hide();
        cardText.show();
        textbox.val('');

        $('#temp').find('.' + card + '_cancel').hide();

        $('#temp').find('.' + card + '_tone').off('click');

        var cardPluses = $('#temp_container').find('.' + card
            + '_plus_container');
        _initCardPlus(cardPluses, card);

        cardPluses = $('#temp_childPlusContainer');
        if (cardPluses.length != 0) {
          _initCardPlus(cardPluses,
              $micro.constants.relation[card].child, true);
        }

        $('#' + parent + '_childPlusContainer').remove();
      };

      var initCancelCard = function (cardType) {
        var card = $('#temp');
        var cancel = card.find('.' + cardType + '_cancel');
        cancel.click(function () {
          var container = card.parent();
          var parentId = container.data('parentId');
          var index = container.data('index');

          if (cardType == 'scene' && $micro.scene.count(parentId) == 0) {
            $('#' + parentId + '_scenesContainer').remove();
            $('#' + parentId + '_placeholder').remove();
            $('#lightbox').click();
          }

          container.remove();

          if ($micro.turn.isAction('playNested')) {
            $.post('PlayNestedCard', {
              gameID: $micro.constants.gameId,
              pass: true
            }, function (json) {
              if (!json.success) {
                // TODO: Undo
                alert('Your card was not able to be added to the database');
                // Just refresh for now
                location.reload();
              } else {
                $micro.turn.progress();
              }
            }, 'json').error(function () {
              alert('Could not connect to server');
              location.reload();
            });
          } else {
            // This works for 'play' and 'playLegacy'

            // Compress items back to location before insert.
            for (var i = $micro[cardType].count(parentId) - 1; i >= index;
                i--) {
              id = $micro[cardType].getIdByIndex(i, parentId);
              // Plus 1 because they are shifted right
              loc = $micro[cardType].getLocationOfCard(i);
              $('#' + id + '_container').attr('style', 'top : ' + loc.y
                  + 'px;left : ' + loc.x + 'px;');
            }

            $micro.support.showPluses();
          } // TODO: Other actions that are possible at the point must handle what happens next if not the default
          $micro.update.unpause();
        });
      };

      // Query is the query for the pluses, card is period or event or scene,
      //  fromParent is if parent is adding, not sibling
      var _initCardPlus = $micro.initCardPlus = function (query, card,
          fromParent) {
        // TODO: Only allow if your turn
        query.click(
            function () {
              // Disable updates
              $micro.update.pause();

              var second = $(this).hasClass(card + '_plus_' + (card != 'event'
                  ? 'right' : 'bottom'));
              var id = $micro.getRootId($(this).attr('id'));
              var parent, index;
              if (fromParent) {
                parent = id;
                index = 0;
              } else {
                parent = cardMap[id].parent;
                index = $micro[card].getIndexById(id, parent);
                if (second) {
                  index++;
                }
              }
              var loc = $micro[card].getLocationOfCard(index);

              var html = $($('#' + card + 'Template').html());
              html.attr('style', 'top : ' + loc.y + 'px;left : ' + loc.x
                  + 'px;');
              html.data('index', index);
              html.data('parentId', parent);

              for (var i = $micro[card].count(parent) - 1; i >= index; i--) {
                id = $micro[card].getIdByIndex(i, parent);
                // Plus 1 because they are shifted right
                loc = $micro[card].getLocationOfCard(i + 1);
                $('#' + id + '_container').attr('style', 'top : ' + loc.y
                    + 'px;left : ' + loc.x + 'px;');
              }
              var parentQuery = $('#' + id + '_container');
              if (!fromParent) {
                parentQuery = parentQuery.parent();
              } else if (card == 'scene') {
                // The parent is the scene container which need to be placed on the page still
                parentQuery = $($('#sceneContainerTemplate').html());
                parentQuery.attr('id', parent + '_scenesContainer');
                $('#scenesHideScrollbar').append(parentQuery);

                var placeholder = _createScenePlaceholder(parent);
                placeholder.click();
              }
              parentQuery.append(html);
              html.find('.' + card + '_tone').click(toneEditable);
              initCancelCard(card);

              $(this).removeClass('highlighted');
              (function (card) {
                html.find('.' + card + '_done').click(function () {
                  cardDone.apply(this, [card]);
                });
              })(card);

              $micro.support.hidePluses();
            }
        );
        query.hover(
            function () {
              $(this).addClass('highlighted');
            },
            function () {
              $(this).removeClass('highlighted');
            }
        );
      };
      // Periods
      _initCardPlus($('.period_plus_container'), 'period');

      // Events
      _initCardPlus($('.event_plus_container'), 'event');
      _initCardPlus($('.period_plus_event_container'), 'event', true);

      //Scenes
      _initCardPlus($('.scene_plus_container'), 'scene');
      _initCardPlus($('.event_plus_scene_container'), 'scene', true);

      var _createScenePlaceholder = function (id) {
        var placeholder = $($('#scenePlaceholderTemplate').html());
        placeholder.attr('id', id + '_placeholder');
        $('#' + id + '_container').prepend(placeholder);
        initScenePlaceholder(placeholder);
        return placeholder;
      };

      var initScenePlaceholder = function (query) {
        query.click(function () {
          var id = $micro.getRootId($(this).attr('id'));
          var container = id + '_scenesContainer';
          $('#lightbox').show();
          $('#scenesHideScrollbar').show();
          $('#' + container).show();
        });
      };
      initScenePlaceholder($('.scenes_placeholder'));

      var _period = $micro.period = {};
      _period.count = function () {
        return periods.length;
      };
      _period.getIdByIndex = function (index) {
        return periods[index] && periods[index].id;
      };
      _period.getIndexById = function (id) {
        return periods.indexOf(cardMap[id]);
      };
      _period.insert = function (id, index, parent) {
        if (index < 0 || index > periods.length) {
          return false;
        }
        var period = {id: id, parent: parent, events: []};

        var end = periods.splice(index);
        periods[index] = period;
        periods = periods.concat(end);
        cardMap[id] = period;
        return true;
      };
      _period.getLocationOfCard = function (index) {
        var spacing = $micro.constants.spacing;
        var x = $micro.constants.left;
        var deltaPeriod = $micro.constants.periodContainerSize.width + spacing;
        x += deltaPeriod * index;
        return {x: x, y: $micro.constants.top};
      };

      var _event = $micro.event = {};
      _event.count = function (parent) {
        return cardMap[parent].events.length;
      };
      _event.getIdByIndex = function (index, parent) {
        var event = cardMap[parent].events[index];
        return event && event.id;
      };
      _event.getIndexById = function (id, parent) {
        return cardMap[parent].events.indexOf(cardMap[id]);
      };
      _event.insert = function (id, index, parent) {
        var events = cardMap[parent].events;
        if (index < 0 || index > events.length) {
          return false;
        }
        var event = {id: id, parent: parent, scenes: []};

        var end = events.splice(index);
        events[index] = event;
        cardMap[parent].events = events.concat(end);

        cardMap[id] = event;
        return true;
      };
      _event.getLocationOfCard = function (index) {
        var spacing = $micro.constants.spacing;
        var top = $micro.constants.periodContainerSize.height + spacing;
        top += index * ($micro.constants.eventContainerSize.height + spacing);
        return {x: 0, y: top};
      };

      _event.createScenePlaceholder = _createScenePlaceholder;

      var _scene = $micro.scene = {};
      _scene.count = function (parent) {
        return cardMap[parent].scenes.length;
      };
      _scene.getIdByIndex = function (index, parent) {
        var scene = cardMap[parent].scenes[index];
        return scene && scene.id;
      };
      _scene.getIndexById = function (id, parent) {
        return cardMap[parent].scenes.indexOf(cardMap[id]);
      };
      _scene.insert = function (id, index, parent) {
        var scenes = cardMap[parent].scenes;
        if (index < 0 || index > scenes.length) {
          return false;
        }
        var scene = {id: id, parent: parent};

        var end = scenes.splice(index);
        scenes[index] = scene;
        cardMap[parent].scenes = scenes.concat(end);

        cardMap[id] = scene;
        return true;
      };
      _scene.getLocationOfCard = function (index) {
        var spacing = $micro.constants.spacing;
        var left = $micro.constants.left;
        left += index * ($micro.constants.sceneContainerSize.width + spacing);
        return {x: left, y: 0};
      };
    })();

    onResize();
    $(window).resize(onResize);
    $(window).scroll(onResize);

    // TODO: Make the rounded corners nicer when showing/hiding.
    $('#yes_button').hover(
        function () {
          $('#yes_list').show();
        },
        function () {
          $('#yes_list').hide();
        }
    );
    $('#no_button').hover(
        function () {
          $('#no_list').show();
        },
        function () {
          $('#no_list').hide();
        }
    );
    $('#lightbox').click(function () {
      $('.scenes_container').hide();
      $('#scenesHideScrollbar').hide();
      $('#legacyContainer').hide();
      $('#createFocus').hide();
      $('#createBigPicture').hide();
      $('#lightbox').hide();
    });
  });
})();

// Init page
$(document).ready(function () {
  var _update = $micro.update = {};
  var shouldPause = false;
  var isPaused = false;
  _update.pause = function () {
    shouldPause = true;
  };
  _update.unpause = function () {
    if (isPaused) {
      shouldPause = false;
      isPaused = false;
      _updateState();
    } else {
      shouldPause = false;
    }
  };

  var checkPause = function () {
    if (shouldPause) {
      shouldPause = false;
      isPaused = true;
    }
    return isPaused;
  };

  // Function for adding card
  var addCard = function (card, id, parent, index, text, tone) {

    var loc = $micro[card].getLocationOfCard(index);

    var html = $($('#' + card + 'Template').html());
    html.attr('id', id + '_container');
    var first = card == 'event' ? 'top' : 'left';
    first = '_' + first + 'PlusContainer';
    var second = card == 'event' ? 'bottom' : 'right';
    second = '_' + second + 'PlusContainer';
    html.find('#temp').attr('id', id);

    html.find('#temp' + first).attr('id', id + first);
    html.find('#temp' + second).attr('id', id + second);
    $micro.initCardPlus(html.find('.' + card + '_plus_container'), card);

    // If not a scene argements 6 is instead true if there are no children
    if (card != 'scene' & arguments[6]) {
      // Set up child plus
      html.find('#temp_childPlusContainer').attr('id', id
          + '_childPlusContainer');
      $micro.initCardPlus(html.find('#' + id + '_childPlusContainer'),
          $micro.constants.relation[card].child, true);
    } else {
      // Remove child plus
      html.find('#temp_childPlusContainer').remove();
    }

    html.find('.' + card + '_textbox').hide();
    var cardText = html.find('.' + card + '_text');
    cardText.show().text(text);
    if (card == 'scene') {
      $(cardText[1]).text(arguments[6]);
      $(cardText[2]).text(arguments[7]);
    }
    if (tone) {
      html.find('.' + card + '_tone').addClass('dark');
    }
    html.find('.' + card + '_done').hide();
    html.find('.' + card + '_cancel').hide();

    html.attr('style', 'top : ' + loc.y + 'px;left : ' + loc.x + 'px;');

    for (var i = $micro[card].count(parent) - 1; i >= index; i--) {
      var siblingId = $micro[card].getIdByIndex(i, parent);
      // Plus 1 because they are shifted right
      loc = $micro[card].getLocationOfCard(i + 1);
      $('#' + siblingId + '_container').attr('style', 'top : ' + loc.y
          + 'px;left : ' + loc.x + 'px;');
    }

    // Default parentQuery
    if (card == 'period') {
      var parentQuery = $('#game');
    } else if (card == 'event') {
      parentQuery = $('#' + parent + '_container');
    } else {
      // The parent is the scene container which may need to be placed on the page still
      var containerId = parent + '_scenesContainer';
      parentQuery = $('#' + containerId);
      if (parentQuery.length == 0) {
        parentQuery = $($('#sceneContainerTemplate').html());
        parentQuery.attr('id', containerId);
        $('#scenesHideScrollbar').append(parentQuery);
        $micro.event.createScenePlaceholder(parent);
      }
      var counter = $('#' + parent + '_placeholder').find(
          '.scenes_placeholder_number');
      var count = Number(counter.text());
      count++;
      counter.text(count);
    }
    parentQuery.append(html);
    $micro[card].insert(id, index, parent);
  };

  // Takes the before plus off start bookend, and the after plus off end bookend
  var bookendsTrimmed = false;
  var paletteNo = 0;
  var paletteYes = 0;
  var _updateState = function () {
    if (checkPause()) {
      return;
    }
    $.post('PlayGame', {
      gameID: $micro.constants.gameId
    }, function (json) {
      if (checkPause()) {
        return;
      }
      // Game setup
      if (!$micro.constants.gameId) {
        $micro.constants.gameId = json.id;
      }
      var oldBigPicture = $('title').text();
      if (json.bigPicture && json.bigPicture != oldBigPicture) {
        $('title').text(json.bigPicture);
        $('#bigPicture').find('span').text(json.bigPicture);
      }
      // TODO: maybe move turn.setPlayer here?
      for (; paletteNo < json.palette_banned.length; paletteNo++) {
        var noItem = $('<li></li>');
        noItem.addClass('item');
        noItem.text(json.palette_banned[paletteNo].description);
        $('#no_list').append(noItem);
      }
      for (; paletteYes < json.palette_recommended.length; paletteYes++) {
        var yesItem = $('<li></li>');
        yesItem.addClass('item');
        yesItem.text(json.palette_recommended[paletteYes].description);
        $('#yes_list').append(yesItem);
      }

      // Only check palette if right round/turn and players action is not set to done (see below)
      var checkPalette = json.round == 0 && json.turn == 3;
      var minPalette = -1;
      var yourPalette = -1;

      // TDOO: This may change if in debug mode. Also should handle logging in as a different user well.
      var players = json.players;
      for (var playerIndex = 0; playerIndex < players.length; playerIndex++) {
        var player = players[playerIndex];
        if (checkPalette) {
          if (minPalette == -1 || player.paletteNum
              < minPalette) {
            minPalette = player.paletteNum;
          }
        }
        if (player.userID == json.userID) {
          $micro.turn.setPlayer(json.userID, player.username);
          if (!checkPalette || player.actionDone) {
            $micro.turn.setActionDone(player.actionDone);
            // If actionDone is true, don't need to check palette.
            checkPalette = false;
            break;
          }
          if (checkPalette) {
            yourPalette = player.paletteNum;
          }
        }
      }
      if (checkPalette) {
        $micro.turn.setActionDone(yourPalette > minPalette);
      }

      var lastCardId = '';
      if (json.lastCardID) {
        lastCardId = $micro.constants.reverseCardEnum[json.last_pes].substring(
            0, 1);
        lastCardId += json.lastCardID;
      }
      var change = $micro.turn.setCurrPlayer(json.round, json.turn,
          json.players, json.legacies);
      $('#focus').find('span').text(json.focus && json.focus.focus);

      var gameId = 'g' + $micro.constants.gameId;
      for (var i = 0; i < json.periods.length; i++) {
        var period = json.periods[i];
        var periodId = 'p' + period.id;
        if ($('#' + periodId).length == 0) {
          addCard('period', periodId, gameId,
              i, period.text, period.tone, period.events.length == 0);
        }
        // TODO: Once edits are allowed, update cards too.
        for (var j = 0; j < period.events.length; j++) {
          var event = period.events[j];
          var eventId = 'e' + event.id;
          if ($('#' + eventId).length == 0) {
            addCard('event', eventId, periodId,
                j, event.text, event.tone, event.scenes.length == 0);
          }
          // TODO: Once edits are allowed, update cards too.
          for (var k = 0; k < event.scenes.length; k++) {
            var scene = event.scenes[k];
            var sceneId = 's' + scene.id;
            if ($('#' + sceneId).length == 0) {
              addCard('scene', sceneId, eventId,
                  k, scene.text, scene.tone, scene.setting, scene.answer);
            }
            // TODO: Once edits are allowed, update cards too.
          }
        }
      }
      var periodCount = json.periods.length;
      if (!bookendsTrimmed && periodCount >= 2) {
        $('#' + 'p' + json.periods[0].id + '_leftPlusContainer').remove();
        $('#' + 'p' + json.periods[periodCount - 1].id
            + '_rightPlusContainer').remove();
        bookendsTrimmed = true;
      }
      if (change) {
        $micro.turn.init(lastCardId);
      }
    }, 'json').error(function () {
      alert('Could not connect to server');
      location.reload();
    });
    setTimeout('$micro.updateState()', 5000);
  };
  $micro.updateState = _updateState;
  _updateState();
});