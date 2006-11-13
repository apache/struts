/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.math.matrix");

// some of this code is based on
// http://www.mkaz.com/math/MatrixCalculator.java
// (published under a BSD Open Source License)
//
// the rest is from my vague memory of matricies in school [cal]
//
// the copying of arguments is a little excessive, and could be trimmed back in
// the case where a function doesn't modify them at all (but some do!)
//
// 2006-06-25: Some enhancements submitted by Erel Segal:
// * addition: a tolerance constant for determinant calculations.
// * performance fix: removed unnecessary argument copying.
// * addition: function "product" for multiplying more than 2 matrices
// * addition: function "sum" for adding any number of matrices
// * bug fix: inversion of a 1x1 matrix without using the adjoint
// * performance fixes: upperTriangle
// * addition: argument "value" to function create, to initialize the matrix with a custom val
// * addition: functions "ones" and "zeros" - like Matlab[TM] functions with the same name.
// * addition: function "identity" for creating an identity matrix of a given size.
// * addition: argument "decimal_points" to function format
// * bug fix: adjoint of a 0-size matrix
// * performance fixes: adjoint
//

dojo.math.matrix.iDF = 0;

// Erel: values lower than this value are considered zero (in detereminant calculations).
// It is analogous to Maltab[TM]'s "eps".
dojo.math.matrix.ALMOST_ZERO = 1e-10;
dojo.math.matrix.multiply = function(a, b){
	var ay = a.length;
	var ax = a[0].length;
	var by = b.length;
	var bx = b[0].length;

	if (ax != by){
		dojo.debug("Can't multiply matricies of sizes "+ax+','+ay+' and '+bx+','+by);
		return [[0]];
	}

	var c = [];
	for(var k=0; k<ay; k++){
		c[k] = [];
		for(var i=0; i<bx; i++){
			c[k][i] = 0;
			for(var m=0; m<ax; m++){
				c[k][i] += a[k][m]*b[m][i];
			}
		}
	}
	return c;
}

// Erel: added a "product" function to calculate product of more than 2 matrices:
dojo.math.matrix.product = function() {
	if (arguments.length==0) {
		dojo.debug ("can't multiply 0 matrices!");
		return 1;
	}
	var result = arguments[0];
	for (var i=1; i<arguments.length; i++){
		result = dojo.math.matrix.multiply(result,arguments[i]);
	}
	return result;
}

// Erel: added a "sum" function to calculate sum of more than 2 matrices:
dojo.math.matrix.sum = function() {
	if (arguments.length==0) {
		dojo.debug ("can't sum 0 matrices!");
		return 0;
	}
	var result = dojo.math.matrix.copy(arguments[0]);
	var rows = result.length;
	if (rows==0) {
		dojo.debug ("can't deal with matrices of 0 rows!");
		return 0;
	}
	var cols = result[0].length;
	if (cols==0) {
		dojo.debug ("can't deal with matrices of 0 cols!");
		return 0;
	}
	for (var i=1; i<arguments.length; ++i) {
		var arg = arguments[i];
		if (arg.length!=rows || arg[0].length!=cols) {
			dojo.debug ("can't add matrices of different dimensions: first dimensions were " + rows + "x" + cols + ", current dimensions are "+arg.length + "x" + arg[0].length);
			return 0;
		}
		
		// The actual addition:
		for (var r=0; r<rows; r++){
			for (var c=0; c<cols; c++){
				result[r][c] += arg[r][c];
			}
		}
	}
	return result;
}


dojo.math.matrix.inverse = function(a){
	// Erel: added special case: inverse of a 1x1 matrix can't be calculated by adjoint
	if (a.length==1 && a[0].length==1){
		return [[ 1 / a[0][0] ]];
	}

	// Formula used to Calculate Inverse:
	// inv(A) = 1/det(A) * adj(A)
	
	var tms = a.length;
	var m = dojo.math.matrix.create(tms, tms);
	var mm = dojo.math.matrix.adjoint(a);
	var det = dojo.math.matrix.determinant(a);
	var dd = 0;

	if(det == 0){
		dojo.debug("Determinant Equals 0, Not Invertible.");
		return [[0]];
	}else{
		dd = 1 / det;
	}

	for (var i = 0; i < tms; i++){
		for (var j = 0; j < tms; j++) {
			m[i][j] = dd * mm[i][j];
		}
	}
	return m;
}

dojo.math.matrix.determinant = function(a){
	if (a.length != a[0].length){
		dojo.debug("Can't calculate the determiant of a non-squre matrix!");
		return 0;
	}

	var tms = a.length;
	var det = 1;
	var b = dojo.math.matrix.upperTriangle(a);

	for (var i=0; i < tms; i++){
		var bii = b[i][i];
		if (Math.abs(bii) < dojo.math.matrix.ALMOST_ZERO){
			return 0;
		}
		det *= bii;
	}
	det = det * dojo.math.matrix.iDF;
	return det;
}

dojo.math.matrix.upperTriangle = function(m){
	m = dojo.math.matrix.copy(m);     // Copy m, because m is changed!
	var f1 = 0;
	var temp = 0;
	var tms = m.length;
	var v = 1;

	//Erel: why use a global variable and not a local variable?
	dojo.math.matrix.iDF = 1;

	for (var col = 0; col < tms - 1; col++) {
		if (typeof m[col][col] != 'number'){
			dojo.debug("non-numeric entry found in a numeric matrix: m["+col+"]["+col+"]="+m[col][col]);
		}
		v = 1;
		var stop_loop = 0;
		
		// check if there is a 0 in diagonal
		while ((m[col][col] == 0) && !stop_loop) {
			// if so,  switch rows until there is no 0 in diagonal:
			if (col + v >= tms){
				// check if switched all rows
				dojo.math.matrix.iDF = 0;
				stop_loop = 1;
			}else{
				for (var r = 0; r < tms; r++) {
					temp = m[col][r];
					m[col][r] = m[col + v][r]; // switch rows
					m[col + v][r] = temp;
				}
				v++; // count row switchs
				dojo.math.matrix.iDF *= -1; // each switch changes determinant factor
			}
		}
		
		// loop over lower-right triangle (where row>col):
		// for each row, make m[row][col] = 0 by linear operations that don't change the determinant:
		for (var row = col + 1; row < tms; row++) {
			if (typeof m[row][col] != 'number'){
				dojo.debug("non-numeric entry found in a numeric matrix: m["+row+"]["+col+"]="+m[row][col]);
			}
			if (typeof m[col][row] != 'number'){
				dojo.debug("non-numeric entry found in a numeric matrix: m["+col+"]["+row+"]="+m[col][row]);
			}
			if (m[col][col] != 0) {
				var f1 = (-1) * m[row][col] / m[col][col];
				// this should make m[row][col] zero:
				// 	m[row] += f1 * m[col];
				for (var i = col; i < tms; i++) {
					m[row][i] = f1 * m[col][i] + m[row][i];
				}
			}
		}
	}
	return m;
}

// Erel: added parameter "value" - a custom default value to fill the matrix with.
dojo.math.matrix.create = function(a, b, value){
	if(!value){
		value = 0;
	}
	var m = [];
	for(var i=0; i<b; i++){
		m[i] = [];
		for(var j=0; j<a; j++){
			m[i][j] = value;
		}
	}
	return m;
}

// Erel implement Matlab[TM] functions "ones" and "zeros"
dojo.math.matrix.ones = function(a,b) { 
	return dojo.math.matrix.create(a,b,1); 
}
dojo.math.matrix.zeros = function(a,b) { 
	return dojo.math.matrix.create(a,b,0); 
}

// Erel: added function that returns identity matrix.
//	size = number of rows and cols in the matrix.
//	scale = an optional value to multiply the matrix by (default is 1).
dojo.math.matrix.identity = function(size, scale){
	if (!scale){
		scale = 1;
	}
	var m = [];
	for(var i=0; i<size; i++){
		m[i] = [];
		for(var j=0; j<size; j++){
			m[i][j] = (i==j? scale: 0);
		}
	}
	return m;
}

dojo.math.matrix.adjoint = function(a){
	var tms = a.length;

	// Erel: added "<=" to catch zero-size matrix
	if (tms <= 1){
		dojo.debug("Can't find the adjoint of a matrix with a dimension less than 2");
		return [[0]];
	}

	if (a.length != a[0].length){
		dojo.debug("Can't find the adjoint of a non-square matrix");
		return [[0]];
	}

	var m = dojo.math.matrix.create(tms, tms);

	var ii = 0;
	var jj = 0;
	var ia = 0;
	var ja = 0;
	var det = 0;
	var ap = dojo.math.matrix.create(tms-1, tms-1);

	for (var i = 0; i < tms; i++){
		for (var j = 0; j < tms; j++){
			ia = 0;
			for (ii = 0; ii < tms; ii++) {   // create a temporary matrix for determinant calc
				if (ii==i){
					continue;       // skip current row
				}
				ja = 0;
				for (jj = 0; jj < tms; jj++) {
					if (jj==j){
						continue;       // skip current col
					}
					ap[ia][ja] = a[ii][jj];
					ja++;
				}
				ia++;
			}
		
			det = dojo.math.matrix.determinant(ap);
			m[i][j] = Math.pow(-1 , (i + j)) * det;
		}
	}
	m = dojo.math.matrix.transpose(m);
	return m;
}

dojo.math.matrix.transpose = function(a){
	var m = dojo.math.matrix.create(a.length, a[0].length);
	for (var i = 0; i < a.length; i++){
		for (var j = 0; j < a[i].length; j++){
			m[j][i] = a[i][j];
		}
	}
	return m;
}

// Erel: added decimal_points argument
dojo.math.matrix.format = function(a, decimal_points){
	if (arguments.length<=1){
		decimal_points = 5;
	}

	function format_int(x, dp){
		var fac = Math.pow(10 , dp);
		var a = Math.round(x*fac)/fac;
		var b = a.toString();
		if (b.charAt(0) != '-'){ b = ' ' + b;}
		var has_dp = 0;
		for(var i=1; i<b.length; i++){
			if (b.charAt(i) == '.'){ has_dp = 1; }
		}
		if (!has_dp){ b += '.'; }
		while(b.length < dp+3){ b += '0'; }
		return b;
	}

	var ya = a.length;
	var xa = ya>0? a[0].length: 0;
	var buffer = '';
	for (var y=0; y<ya; y++){
		buffer += '| ';
		for (var x=0; x<xa; x++){
			buffer += format_int(a[y][x], decimal_points) + ' ';
		}
		buffer += '|\n';
	}
	return buffer;
}

dojo.math.matrix.copy = function(a){
	var ya = a.length;
	var xa = a[0].length;
	var m = dojo.math.matrix.create(xa, ya);
	for (var y=0; y<ya; y++){
		for (var x=0; x<xa; x++){
			m[y][x] = a[y][x];
		}
	}
	return m;
}

dojo.math.matrix.scale = function(k, a){
	a = dojo.math.matrix.copy(a);  // Copy a because a is changed!
	var ya = a.length;
	var xa = a[0].length;

	for (var y=0; y<ya; y++){
		for (var x=0; x<xa; x++){
			a[y][x] *= k;
		}
	}
	return a;
}
