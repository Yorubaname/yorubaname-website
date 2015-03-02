<html>
<head>
<script src="../assets/js/jquery-2.1.3.min.js"></script>
<script type="text/javascript">
    $(function() {
        $("#submit").click(function() {
            $("#flashcard").html("");
            $.ajax({
                type: 'POST',
                url: "/v1/name",
                data: $("#entryForm").serialize(),
                success: function (a,b,c) {
                    $("#entryForm")[0].reset();
                },
                error: function (response) {
                    $("#flashcard").html("<span style='color:red'>"+response.responseText+"</span>");
                    $("#entryForm")[0].reset();
                }
             });


        });



        $("#uploadButton").click(function () {
                $("#formflashcard").html("Uploading...");
                var data = new FormData();
                data.append('nameFiles', document.uploadForm.nameFiles.files[0]);

                $.ajax({
                  type: 'POST',
                  url: "v1/names/upload",
                  data: data,
                  cache: false,
                  contentType: false,
                  processData: false,
                  success: function(response) {
                  console.log(response);
                   $("#formflashcard").html(response.numberOfNamesUpload + " names uploaded" + "<a href='/v1/names'> View them</a>");
                    $("#uploadForm")[0].reset();
                  },
                  error: function (response) {
                      $("#formflashcard").html("Error uploading");
                  }
                });

        });

        $("#submitlookup").click(function(){
            var name = $("#namelookup").val();
            var ischecked = $("#showDuplicates").is(':checked');
            if (ischecked) {
                window.location = "/v1/names/"+name+"?duplicates=true";
            } else {
                window.location = "/v1/names/"+name;
            }
        });


        $("#clearButton").click(function() {
                    $("#flashcard").html("");
                    $.ajax({
                        type: 'GET',
                        url: "/v1/names/delete",
                        success: function (a,b,c) {
                            $("#entryForm")[0].reset();
                        },
                        error: function (response) {
                            $("#flashcard").html("<span style='color:red'>"+response.responseText+"</span>");
                            $("#entryForm")[0].reset();
                        }
                     });


        });

    });
</script>
</head>

<body style="margin-top:0px">
<div style="background-color:#333; font-size:24px; color: #fff; padding: 10px;">Dashboard</div>
<h2>Name Lookup</h2>
<em>This is not the search functionality. It would soon be. This right now should be used to retirve a particular name from the database to view</em><br/>
<input type="text" id="namelookup"/><input type="checkbox" value="true" id="showDuplicates"> show duplicate entries
<br/><input id="submitlookup" type="button" value="Look Up"/>
<h2>Entered Names </h2>
<a href="/v1/names">View all names</a> | <input type="button" id="clearButton" value="Clear Names" />

<hr/>

<h2>Individual Entry stub </h2>
<hr/>
<form id="entryForm">

    <div id="flashcard" style="text-transform: capitalize;"></div>

    Name:<br>
    <input type="text" name="name"><br><br>
    Tone Mark:<br>
    <input type="text" name="tonalMark"><br><br>
    Meaning:<br>
    <textarea name="meaning"></textarea><br><br>
    Morphology:<br>
    <textarea name="morphology"></textarea><br><br>
    Area of origin:<br>
    <select name="geoLocation">
        <option value="awori">Awori</option>
        <option value="egba">Egba</option>
        <option value="ijesha">Ijesha</option>
    </select>

    <br><br>
    <input type="button" id="submit" value="Submit Entry">

</form>

<br/>

<h2>Form upload stub </h2>
<hr/>
<div id="formflashcard" style="text-transform: capitalize; color:red"></div><br/>
<form action="/v1/names/upload" id="uploadForm" name="uploadForm">
Upload Excel file
<input type="file" name="nameFiles" id="uploadFile" />
<input type="button" id="uploadButton" value="Upload Names" />

</form>

<h2>Form Entry</h2>
<hr/>
Download Form entry template <a href="/name_entry_template.xlsx">here</a>


</body>
</html>