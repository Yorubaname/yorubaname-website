<#import "decorator.ftl" as decorator />
<#import "/spring.ftl" as spring />

<@decorator.layout>
<div class="container"><!--start container -->
    <div class="header">
        <div class="row">
            <div class="logo span3 pull-left">
                <h1><a href="index.html"><img src="assets/img/logo.png" alt="YorubaName.com"></a></h1>
            </div>
            <div class="search span4" align="center">
                <form id="search-name-entry">
                    <i class="fa fa-search"></i>
                    <input type="search" placeholder="Search name..." name="search">
                </form>
            </div>
            <div class="social span2 pull-right" align="center">
                <p style="font-size:12px"><a href="#">SignUp</a> | <a href="#">Login</a>&nbsp;<br>Connect with us</p>
                <a href="#" target="_blank" title="Facebook"><i class="fa fa-facebook"></i></a> &nbsp;&nbsp;
                <a href="#" target="_blank" title="Twitter"><i class="fa fa-twitter"></i></a> &nbsp;&nbsp;
                <a href="#" target="_blank" title="Google+"><i class="fa fa-google-plus"></i></a>
            </div>
        </div>
    </div>

    <div class="well">
        <div class="row">
            <div class="h-form-entry span5 pull-left">
                <div class="span2 h-names popular-male-names" align="center">
                    <h6>POPULAR MALE NAMES</h6>

                    <p><a href="#">Kola</a></p>

                    <p><a href="#">Dade</a></p>

                    <p><a href="#">Akin</a></p>

                    <p><a href="#">Alabi</a></p>

                    <p><a href="#">Anyname</a></p>

                    <p><a href="#">More...</a></p>
                </div>
                <div class="span2 h-names popular-female-names" align="center">
                    <h6>POPULAR FEMALE NAMES</h6>

                    <p><a href="#">Biola</a></p>

                    <p><a href="#">Iya Basira</a></p>

                    <p><a href="#">Shola</a></p>

                    <p><a href="#">Kemi</a></p>

                    <p><a href="#">Bimbo</a></p>

                    <p><a href="#">More...</a></p>
                </div>
                <div class="span2 h-names recent-names" align="center">
                    <h6>RECENT NAMES</h6>

                    <p><a href="#">Koko</a></p>

                    <p><a href="#">Tiwa</a></p>

                    <p><a href="#">Olu</a></p>

                    <p><a href="#">Simbi</a></p>

                    <p><a href="#">Laide</a></p>

                    <p><a href="#">More...</a></p>
                </div>
            </div>
            <div class="adspace span3 pull-right" align="center">
                <h3>Ad space</h3>
            </div>

            <div class="span5 todays-quote" align="center">
                <hr>
                <p>Do you want to add a name to the dictionary? <a href="#">Click here</a></p>
            </div>

            <div class="span5" align="center">
                <hr>
                <h3>Todays Quote</h3>

                <p>Kile Oruko Yoruba nisi nisi mgbati - Baba Aremu</p>
            </div>
        </div>
    </div>

    <!-- footer starts here... -->
    <div class="footer pull-right">
        <p>&copy; 2015 YorubaName.com. All rights reserved.</p>
    </div>
</div><!-- close container -->
</@decorator.layout>