var main = function () {
	var fill = d3.scale.category20();

	document.getElementById( "displayNumber" ).innerHTML = clusters.length;
	document.getElementById( "inputNumber" ).max = clusters.length;

	var layout = d3.layout.cloud()
		.size([600, 600])
		.padding(5)
		.rotate(function() { return Math.round( Math.random()*8 )*(180/8) - 90; })
		.font("Impact")
		.fontSize( function(d) { return d.size; } )
		.on("end", draw);

	var anzeigenI = function ( i ) {
		var words = clusters[ i ].terms;
		layout.stop().words( words.map(function(d) {
			return {text: d, size: 20 + Math.random() * 40 };
		}) ).start();
		document.getElementById( "displayDocuments" ).innerHTML = clusters[ i ].documents.join( '<br/>' );
	};

	anzeigenI( 0 );

	var anzeigen = function () {
		anzeigenI( document.getElementById( "inputNumber" ).value - 1 );
	};
	document.getElementById( "inputAnzeigen" ).addEventListener( 'click', anzeigen, false );

	function draw(words) {
		var canvas = document.getElementById( "canvasTD" );
		canvas.removeChild( canvas.children[ 0 ] );

		d3.select("#canvasTD").append("svg")
			.attr("width", 600)
			.attr("height", 600)
			.append("g")
			.attr("transform", "translate(300,300)")
			.selectAll("text")
			.data(words)
			.enter().append("text")
			.style("font-size", function(d) { return d.size + "px"; })
			.style("font-family", "Impact")
			.style("fill", function(d, i) { return fill(i); })
			.attr("text-anchor", "middle")
			.attr("transform", function(d) {
			return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
		})
		.text(function(d) { return d.text; });
	}

};

