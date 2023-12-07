                    <body>
                      <h1 class="text-3xl font-bold hover:underline text-red-600">$test0</h1>
                      <button id="colorButton" class="bg-blue-500">Cock</button>
                      <script>
                        var button = document.getElementById('colorButton');
                        button.addEventListener('click', function() {
                          javafxBridge.externalFunction();
                        });
                      </script>
                    </body>
                  </html>
                  <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <script src="https://cdn.tailwindcss.com"></script>
                          <body >
                              <div class="flex justify-center items-center h-screen w-full bg-slate-700">
                            <div class="rounded-full bg-teal-600 h-1/3 w-1/2 border-8 border-teal-400">
                          </div>
                            </div>
                          </body>
                        </html>
                  """.stripMargin
              )
              prefWidth = 400
              prefHeight = 200
              engine.executeScript(
                "window.javafxBridge = { externalFunction: function() { javafxBridge.externalFunction(); } }"
              )
              prefWidth = 800
              prefHeight = 600
            }
          )
        }
      }
    }
  }
}
