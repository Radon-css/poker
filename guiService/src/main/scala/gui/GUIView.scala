/*package de.htwg.poker.gui

import de.htwg.poker.gui.types.GUICard
import de.htwg.poker.gui.types.GUIGameState
import de.htwg.poker.gui.types.GUIPlayer
import de.htwg.poker.gui.types.GUIRank
import de.htwg.poker.gui.types.GUIRank.*
import de.htwg.poker.gui.types.GUISuit
import de.htwg.poker.gui.types.GUISuit.*
import javafx.concurrent.Worker.State
import javafx.scene.web.WebEngine
import netscape.javascript.JSObject
import scala.compiletime.ops.boolean
import scala.util.{Failure, Success, Try}
import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.scene.web.WebView

object GUIView {
  def getView(
      handEval: String,
      gameState: GUIGameState
  ): String = {

    val playerListHtml = updatePlayersHtml(gameState)
    val cardListHtml = updateCardsHtml(gameState)
    val boardListHtml = updateBoardHtml(gameState)
    val betListHtml = updateBetsHtml(gameState)

    val gameStarted = gameState.players.getOrElse(List.empty[GUIPlayer]).size != 0

    var balance = 0;
    if (gameStarted) {
      balance = gameState.currentHighestBetSize - gameState.players.getOrElse(List.empty[GUIPlayer])(gameState.playerAtTurn).currentAmountBetted
    }

    return s"""
    ${if (gameStarted) {
      s"""
          <!DOCTYPE html>
          <html>
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <script src="https://cdn.tailwindcss.com"></script>
            </head>
            <body class="flex flex-col">
            <div class="flex flex-col justify-center items-center h-screen w-full bg-gradient-to-tl from-gray-800 to-gray-700 bg-gradient-to-r space-y-5">
              <div class="flex items-center justify-between w-full h-14">
              <div class="flex space-x-2 ml-2 ">
                <button class="mt-4 ml-4 font-extrabold h-12 w-16 my-5 text-slate-100 bg-gray-600/40 rounded-full hover:bg-gray-600/20 flex justify-center items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-undo"><path d="M3 7v6h6"/><path d="M21 17a9 9 0 0 0-9-9 9 9 0 0 0-6 2.3L3 13"/></svg>
                </button>
                <button class="mt-4 font-extrabold h-12 w-16 my-5 text-slate-100 rounded-full bg-gray-600/40 hover:bg-gray-600/20 flex justify-center items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-redo"><path d="M21 7v6h-6"/><path d="M3 17a9 9 0 0 1 9-9 9 9 0 0 1 6 2.3l3 2.7"/></svg>
                </button>
              </div>
              <div class="flex flex-col items-center justify-center">
              <h1 class="text-gray-100">Current Hand:</h1>
              <h1 class="text-red-500">${handEval}</h1>
              </div>
                <button class="flex justify-start space-x-2 items-center mt-4 mr-4 font-bold h-12 w-36 my-5 text-slate-100 rounded-full bg-gray-600/40 hover:bg-gray-600/20" onclick="restartGame()">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-arrow-clockwise ml-4" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M8 3a5 5 0 1 0 4.546 2.914.5.5 0 0 1 .908-.417A6 6 0 1 1 8 2z"/>
                <path d="M8 4.466V.534a.25.25 0 0 1 .41-.192l2.36 1.966c.12.1.12.284 0 .384L8.41 4.658A.25.25 0 0 1 8 4.466"/>
                </svg>
                <div>RESTART</div>
                </button>
            </div>
                <div class="flex space-x-56">
                ${playerListHtml(0)}
                ${playerListHtml(1)}
              </div>
              <div class="flex justify-center items-center h-64 w-full">
                ${playerListHtml(5)}
                <div class="flex flex-col items-center rounded-full bg-teal-600 h-72 w-3/5 border-8 border-teal-400 shadow-[inset_0_-2px_8px_rgba(0,0,0,0.8)]">
                    <div class="flex mt-4 space-x-48">
                      <div class="flex h-10 w-12">
                        ${cardListHtml(0)._1}
                        ${cardListHtml(0)._2}
                      </div>
                      <div class="flex h-10 w-12">
                        ${cardListHtml(1)._1}
                        ${cardListHtml(1)._2}
                      </div>
                    </div>

                  <div class ="flex space-x-36">
                      ${betListHtml(0)}
                      ${betListHtml(1)}
                  </div>

                  <div class = "flex justify-center items-center space-x-12 mt-6">

                    <div class=" flex items-center space-x-2">
                      <div class="flex h-10 w-12">
                        ${cardListHtml(5)._1}
                        ${cardListHtml(5)._2}
                      </div>
                        ${betListHtml(5)}
                    </div>

                      <div class="flex flex-col items-center space-y-2">
                        <p class="rounded-full bg-slate-100 px-2">${gameState.pot + "$"}
                        </p>
                        <div class="flex px-16">
                        ${boardListHtml(0)}
                        ${boardListHtml(1)}
                        ${boardListHtml(2)}
                        ${boardListHtml(3)}
                        ${boardListHtml(4)}
                        </div>
                      </div>

                    <div class=" flex items-center space-x-2">
                        ${betListHtml(2)}
                      <div class="flex h-10 w-12">
                        ${cardListHtml(2)._1}
                        ${cardListHtml(2)._2}
                      </div>
                    </div>
                    </div>

                    <div class ="flex space-x-36 mt-6">
                        ${betListHtml(4)}
                        ${betListHtml(3)}
                  </div>

                    <div class = "flex mb-4 space-x-48 mt-1">
                    <div class="flex h-10 w-12">
                            ${cardListHtml(4)._1}
                            ${cardListHtml(4)._2}
                    </div>
                    <div class="flex h-10 w-12">
                            ${cardListHtml(3)._1}
                            ${cardListHtml(3)._2}
                    </div>
                    </div>
                </div>
                ${playerListHtml(2)}
              </div>
              <div class="flex space-x-56">
                ${playerListHtml(4)}
                ${playerListHtml(3)}
              </div>
              <div class="flex space-x-8 items-center">
              <button class=" flex justify-start space-x-2 items-center w-28 h-12 font-bold my-5 bg-red-600/20 text-red-500  rounded-full hover:bg-red-600/10" onclick="fold()">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-x-circle ml-4" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
                  <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"/>
                </svg>
                <div class="flex justify-center items-center">FOLD</div>
              </button>
              <button class="flex justify-start space-x-2 items-center w-28 h-12 font-bold my-5 bg-blue-600/20 text-blue-400 rounded-full  hover:bg-blue-600/10" onclick="check()">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-check2-circle ml-3" viewBox="0 0 16 16">
                  <path d="M2.5 8a5.5 5.5 0 0 1 8.25-4.764.5.5 0 0 0 .5-.866A6.5 6.5 0 1 0 14.5 8a.5.5 0 0 0-1 0 5.5 5.5 0 1 1-11 0"/>
                  <path d="M15.354 3.354a.5.5 0 0 0-.708-.708L8 9.293 5.354 6.646a.5.5 0 1 0-.708.708l3 3a.5.5 0 0 0 .708 0l7-7z"/>
                </svg>
                <div class="flex justify-center items-center">CHECK</div>
              </button>
              <button class="flex justify-start space-x-2 items-center w-32 h-12 font-bold my-5 bg-green-600/20 text-green-400 rounded-full  hover:bg-green-600/10" onclick="call()">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-arrow-right-circle ml-2" viewBox="0 0 16 16">
                  <path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8m15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0M4.5 7.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5z"/>
                </svg>
                <div class="flex justify-center items-center">CALL ${balance + "$"}</div>
              </button>
              <form onsubmit="bet()" class="flex flex-row items-center">
                <button type="submit" class="flex justify-start space-x-2 items-center w-28 h-12 font-bold my-5 bg-yellow-600/20 text-yellow-400 rounded-l-full hover:bg-yellow-600/10">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-arrow-up-circle ml-4" viewBox="0 0 16 16">
                  <path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8m15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0m-7.5 3.5a.5.5 0 0 1-1 0V5.707L5.354 7.854a.5.5 0 1 1-.708-.708l3-3a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 5.707z"/>
                </svg>
                <div>BET</div>
              </button>
                <input type="number" id="betInput" name="fname" placeholder="Enter betsize" class=" h-12 w-28 bg-slate-600 rounded-r-full px-2 py-1 focus:none text-white">
              </form>
              </div>
              <script>
                function call() {
                  invoke.call();
                }
                function check() {
                  invoke.check();
                }
                function fold()  {
                  invoke.fold();
                }
                function bet() {
                  invoke.bet(document.getElementById("betInput").value);
                }
                function restartGame() {
                  invoke.restartGame();
                }
              </script>
              </body>
          </html>
              """
    } else {
      s"""
              <!DOCTYPE html>
          <html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.tailwindcss.com"></script>
  </head>
  <body class="flex flex-col">
   <form onsubmit="startGame()">
    <div class="flex flex-col justify-center items-center h-screen w-full  bg-gradient-to-tl from-gray-800 to-gray-700 bg-gradient-to-r  space-y-5">
      <div class="flex flex-col justify-center items-center">
      <h1 class="text-xl font-bold text-gray-300/80">Insert Playernames</h1>
      <h1 class="text-xl font-bold text-gray-300/80">Insert small and big Blind</h1>
      <h1 class="text-xl font-bold text-gray-300/80 mb-4">Press start to play</h1>
    </div>
      <div class="flex space-x-56">
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm('form1')">
        <div id="outerDiv1" class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg id="innerDiv1" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100" viewBox="0 0 16 16 ">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form1" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
    <div class="flex flex-col justify-center items-center">
      <button onClick="revealForm('form2')">
      <div id="outerDiv2" class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
      <svg id="innerDiv2" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
        <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
        <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
      </svg>
    </div>
  </button>
    <input type="string" id="form2" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
  </div>
    </div>
    <div class="flex justify-center items-center h-64 w-full">
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm('form3')">
        <div id="outerDiv3" class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg id="innerDiv3" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form3" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
      <div class="flex flex-col items-center justify-center rounded-full bg-teal-600 h-72 w-3/5 border-8 border-teal-400 shadow-[inset_0_-2px_8px_rgba(0,0,0,0.8)]">
      <h1 class="text-9xl font-extrabold text-black/20 italic">POKER</h1>
      <div class="flex justify-center items-center w-full space-x-1 mt-4">
        <input type="number" id="smallBlind" name="fname" placeholder="smallBlind" class="h-8 w-24 bg-black/30 rounded-full focus:none text-white text-center hover:bg-black/20">
        <input type="number" id="bigBlind" name="fname" placeholder="bigBlind" class="h-8 w-24 bg-black/30 rounded-full focus:none text-white text-center hover:bg-black/20">
      </div>
      </div>
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm('form4')">
        <div id="outerDiv4" class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg id="innerDiv4" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form4" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
    </div>
    <div class="flex space-x-56">
      <div class="flex flex-col justify-center items-center">
        <button onClick="revealForm('form5')">
        <div id="outerDiv5" class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
        <svg id="innerDiv5" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
          <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
          <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
        </svg>
      </div>
    </button>
      <input type="string" id="form5" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
    </div>
    <div class="flex flex-col justify-center items-center">
      <button onClick="revealForm('form6')">
      <div id="outerDiv6" class="rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5">
      <svg id="innerDiv6" xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-person-fill-add text-slate-100 hover:w-10 hover:h-10" viewBox="0 0 16 16">
        <path d="M12.5 16a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7m.5-5v1h1a.5.5 0 0 1 0 1h-1v1a.5.5 0 0 1-1 0v-1h-1a.5.5 0 0 1 0-1h1v-1a.5.5 0 0 1 1 0m-2-6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
        <path d="M2 13c0 1 1 1 1 1h5.256A4.493 4.493 0 0 1 8 12.5a4.49 4.49 0 0 1 1.544-3.393C9.077 9.038 8.564 9 8 9c-5 0-6 3-6 4"/>
      </svg>
    </div>
  </button>
    <input type="string" id="form6" name="fname" placeholder="Playername" class="h-8 w-28 bg-transparent rounded-md focus:none text-white text-center" style="visibility:hidden;">
  </div>
    </div>
    <div class="flex space-x-8 items-center">
     <button id="outerDiv7"type="submit" class="w-28 h-12 font-bold my-5 bg-gray-300/80 text-slate-700 rounded-full hover:text-gray-100 hover:bg-gray-600 shadow-lg">
      <div class="flex justify-center items-center space-x-1">
      <svg id="innerDiv7" xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="currentColor" class="bi bi-play-fill hover:w-10 hover:h-10" viewBox="0 0 16 16">
        <path d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393z"/>
      </svg>
    </div>
  </button>
  </div>
</form>
    <script>
  function startGame() {
    invoke.toList(
      document.getElementById("form1").value,
      document.getElementById("form2").value,
      document.getElementById("form3").value,
      document.getElementById("form4").value,
      document.getElementById("form5").value,
      document.getElementById("form6").value,
      document.getElementById("smallBlind").value,
      document.getElementById("bigBlind").value
    );
  }
  function revealForm(formId) {
   event.preventDefault();
   var element = document.getElementById(formId);
   element.style.visibility = (element.style.visibility === "hidden") ? "visible" : "hidden";
   document.getElementById(formId).focus();
}
  function addHoverEffect(outerId, innerId) {
      var outerDiv = document.getElementById(outerId);
      var innerDiv = document.getElementById(innerId);

  outerDiv.addEventListener('mouseenter', function() {
      innerDiv.classList.add('w-10', 'h-10');
    });

  outerDiv.addEventListener('mouseleave', function() {
      innerDiv.classList.remove('w-10', 'h-10');
    });
  }
  addHoverEffect('outerDiv1', 'innerDiv1');
  addHoverEffect('outerDiv2', 'innerDiv2');
  addHoverEffect('outerDiv3', 'innerDiv3');
  addHoverEffect('outerDiv4', 'innerDiv4');
  addHoverEffect('outerDiv5', 'innerDiv5');
  addHoverEffect('outerDiv6', 'innerDiv6');
  addHoverEffect('outerDiv7', 'innerDiv7');
  </script>
  </body>
</html>
        """
    }}
    """
  }
  /* these are methods to create the new Html Code that has to be displayed in the GUI when the GameState is updated.
     in GUIView, we then simply pass the new Html Code that has been created by these methods into our static Html code with the help of String Variables.*/

  def updatePlayersHtml(gameState: GUIGameState): List[String] = {
    val newPlayerList =
      gameState.players.getOrElse(List.empty[GUIPlayer]).map(playerToHtml)
    List
      .fill(6)(HiddenHtml)
      .patch(0, newPlayerList, newPlayerList.size)
  }

  def updateCardsHtml(gameState: GUIGameState): List[(String, String)] = {
    val playerList =
      gameState.players.getOrElse(List.empty[GUIPlayer]).zipWithIndex
    val playerAtTurn = gameState.playerAtTurn
    val newCardList = playerList.map {
      case (player, index) if index == playerAtTurn =>
        (cardToHtml(player.card1), cardToHtml(player.card2))
      case _ =>
        (HiddenPlayerCardHtml, HiddenHtml)
    }

    val defaultCardListHtml = List.fill(6)(
      (HiddenHtml, HiddenHtml)
    )

    defaultCardListHtml.patch(0, newCardList, newCardList.size)
  }

  def betSizeToHtml(amount: Int) = {
    s""" <div class = \"rounded-full bg-gray-700/80 h-6 w-11\">
                      <div class = \"flex items-center justify-center py-0\">
                      <h1 class=\"text-gray-400\">${amount}$$</h1>
                    </div>
                    </div>
                    """
  }

  def updateBoardHtml(gameState: GUIGameState): List[String] = {
    val boardList = gameState.board
    val newBoardList = boardList.map(cardToHtml)
    val invisBoardList = List.fill(5)(HiddenHtml)
    val hiddenBoardList =
      List.fill(5)(HiddenBoardCardHtml)

    if (gameState.players.getOrElse(List.empty[GUIPlayer]).isEmpty) invisBoardList
    else hiddenBoardList.patch(0, newBoardList, newBoardList.size)
  }

  def updateBetsHtml(gameState: GUIGameState): List[String] = {
    val playerList = gameState.players.getOrElse(List.empty[GUIPlayer])
    val newBetList =
      playerList.map(player => betSizeToHtml(player.currentAmountBetted))
    val hiddenBetList = List.fill(6)(HiddenHtml)

    hiddenBetList.patch(0, newBetList, playerList.size)
  }

  def suitToHtml(suit: GUISuit): String = suit match {
    case Clubs =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-club-fill\" viewBox=\"0 0 16 16\"><path d=\"M11.5 12.5a3.493 3.493 0 0 1-2.684-1.254 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907 3.5 3.5 0 1 1-2.538-5.743 3.5 3.5 0 1 1 6.708 0A3.5 3.5 0 1 1 11.5 12.5\"/></svg>"
    case Spades =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-suit-spade-fill\" viewBox=\"0 0 16 16\"><path d=\"M7.184 11.246A3.5 3.5 0 0 1 1 9c0-1.602 1.14-2.633 2.66-4.008C4.986 3.792 6.602 2.33 8 0c1.398 2.33 3.014 3.792 4.34 4.992C13.86 6.367 15 7.398 15 9a3.5 3.5 0 0 1-6.184 2.246 19.92 19.92 0 0 0 1.582 2.907c.231.35-.02.847-.438.847H6.04c-.419 0-.67-.497-.438-.847a19.919 19.919 0 0 0 1.582-2.907\"/></svg>"
    case Diamonds =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-diamond-fill\" viewBox=\"0 0 16 16\"><path fill-rule=\"evenodd\" d=\"M6.95.435c.58-.58 1.52-.58 2.1 0l6.515 6.516c.58.58.58 1.519 0 2.098L9.05 15.565c-.58.58-1.519.58-2.098 0L.435 9.05a1.482 1.482 0 0 1 0-2.098L6.95.435z\"/></svg>"
    case Hearts =>
      "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"red\" class=\"bi bi-suit-heart-fill\" viewBox=\"0 0 16 16\"><path d=\"M4 1c2.21 0 4 1.755 4 3.92C8 2.755 9.79 1 12 1s4 1.755 4 3.92c0 3.263-3.234 4.414-7.608 9.608a.513.513 0 0 1-.784 0C3.234 9.334 0 8.183 0 4.92 0 2.755 1.79 1 4 1\"/></svg>"
  }

  def cardToHtml(card: GUICard): String = {
    s"<div class=\"rounded-lg bg-slate-100 w-6 h-9 hover:scale-125 flex flex-col justify-center items-center shadow-xl shadow-black/50\">${suitToHtml(
      card.suit
    )}<h1 class=\"font-bold \">${card.rank.toString}</h1></div>"
  }

  def playerToHtml(player: GUIPlayer) = {
    val opacityClass = if (player.folded) "opacity-50" else ""
    s"""<div class=\"flex flex-col items-center justify-center space-x-2\">
                <div class=\"rounded-full bg-gray-600 h-16 w-16 flex justify-center items-center text-white ml-1.5 ${opacityClass}\">
                  <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" fill=\"currentColor\" class=\"bi bi-person-fill\" viewBox=\"0 0 16 16\">
                    <path d=\"M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6\"/>
                  </svg>
                </div>
                    <p class=\"p-1 text-slate-100\">${player.playername}</p>
                    <div class=\"flex items-center justify-center rounded-full bg-slate-100 text-gray-700 w-14\">
                      <p class=\"p-1\">${player.balance}$$</p>
                    </div>
              </div>"""
  }

  val HiddenPlayerCardHtml =
    """<svg class="ml-3" width="42" height="42" viewBox="0 0 119 119" fill="none" xmlns="http://www.w3.org/2000/svg">
     |  <path fill-rule="evenodd" clip-rule="evenodd" d="M38.9596 14H14C6.26801 14 0 20.268 0 28V105C0 112.732 6.26801 119 14 119H44.4789L16.2797 110.439C8.88111 108.193 5.16362 100.534 7.97644 93.3319L38.9596 14Z" fill="#2dd4bf"/>
     |  <rect width="67.2466" height="106.582" rx="14" transform="matrix(0.95688 0.290485 -0.363791 0.931481 54.6531 0)" fill="#2dd4bf"/>
     |</svg>""".stripMargin

  val HiddenBoardCardHtml =
    "<div class=\"rounded-lg bg-teal-400 w-6 h-9\"></div>"

  val HiddenHtml = "<div class=\"hidden\"> </div>"

} */
