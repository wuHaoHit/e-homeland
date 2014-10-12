/*!

Holder - 2.3.2 - client side image placeholders
(c) 2012-2014 Ivan Malopinsky / http://imsky.co

Provided under the MIT License.
Commercial use requires attribution.

*/
$("#exit").click(function(){
	$.post("/logout",
	{
		cmd:"logout"
	});
	window.location.href="/";
});

window.onbeforeunload = function (e) {
	var e = e || window.event;
	/*if(e){
		e.returnValue = 'Are you sure?';
	}
	$.post("/logout",
	{
		cmd:"logout"
	});*/
}

$(document).ready(function(){
	$(".nav.nav-sidebar-header").click(function(){
		$(this).next("ul").toggle(100);
	});
});
