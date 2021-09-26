console.log("this is script file");
const toggleSidebar = () => {

	if ($(".sidebar").is(":visible")) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");

	}
	else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

/*const search = () => {
	console.log("searching.....");
	let query = $("#search-input").val();
	if (query == "") {
		$(".search-result").hide();
	}
	else {
		console.log(query);
		let url = 'http://localhost:8080/search/${query}';
		fetch(url)
		.then((response)=> { return respose.json();
 })
.then((data) => {
	console.log(data);
	let text='<div class='list-group'>';
	data.forEach((contact)=>{
	text +='<a href='/user/{contact.cId}/contact' class='list-group-item list-group-action'>${contact.name}</a>'
	});
	text +='</div>';
	$(".search-result").html(text);
	});
     $(".search-result").show();
}
};
*/

const paymentStart = () =>{
console.log("payment started.....");
let ammount=$("#payment_field").val();
console.log(amount);
if(amount=='' || amount==null)
{
alert("amount is required")
return ;
}
$.ajax({
url:'/user/create_order',
data:JSON.stringfy({amount:amount,info:'order_request'}),
contentType:'application/json',
type:'POST',
dataType:'json',
success:function(success){
console.log(response)
if(response.status=="created")
{
let options={
"key": "YOUR_KEY_ID", // Enter the Key ID generated from the Dashboard
    "amount": "50000", // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
    "currency": "INR",
    "name": "Acme Corp",
    "description": "Test Transaction",
    "image": "https://example.com/your_logo",
    "order_id": "order_9A33XWu170gUtm", //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
    "handler": function (response){
        alert(response.razorpay_payment_id);
        alert(response.razorpay_order_id);
        alert(response.razorpay_signature)
    },
    "prefill": {
        "name": "",
        "email": "",
        "contact": ""
    },
    "notes": {
        "address": "Razorpay Corporate Office"
    },
    "theme": {
        "color": "#3399cc"
    },
};
var rzp1 = new Razorpay(options);
rzp1.on('payment.failed', function (response){
        console.log(response.error.code);
        console.log(response.error.description);
        console.log(response.error.source);
        console.log(response.error.step);
        console.log(response.error.reason);
        console.log(response.error.metadata.order_id);
        console.log(response.error.metadata.payment_id);
        alert("opps payment failed");
});
rzp.open();
}
},
error:function(error){
console.log(error)
alert("something went wrong")

}
})
};