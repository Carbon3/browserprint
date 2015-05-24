/*
 * Code taken from from Am I Unique?
 * https://amiunique.org
 * 
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Pierre Laperdrix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

//WebGL fingerprinting
var camera, scene, renderer;
try {
    init();
    renderer = animate();
    webGLData = renderer.domElement.toDataURL("image/png");

    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");
    if(ctx.getSupportedExtensions().indexOf("WEBGL_debug_renderer_info") >= 0) {
        webGLVendor = ctx.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_VENDOR_WEBGL);
        webGLRenderer = ctx.getParameter(ctx.getExtension('WEBGL_debug_renderer_info').UNMASKED_RENDERER_WEBGL);
    } else {
        webGLVendor = "Not supported";
        webGLRenderer = "Not supported";
    }
} catch(e){
    webGLData = "Not supported";
    webGLVendor = "Not supported";
    webGLRenderer = "Not supported";
}

function init() {

    //Init scene,camera and materials
    var darkMaterial = new THREE.MeshBasicMaterial( { color: 0xffffcc } );
    var wireframeMaterial = new THREE.MeshBasicMaterial( { color: 0x000000, wireframe: true, transparent: true } );
    var multiMaterial = [ darkMaterial, wireframeMaterial ];

    renderer = new THREE.WebGLRenderer();
    renderer.setSize( 1000, 400 );
    $("#canvas2").append(renderer.domElement);

    camera = new THREE.PerspectiveCamera( 70, window.innerWidth / window.innerHeight, 1, 1000 );
    camera.position.z = 125;
    scene = new THREE.Scene();

    //Cube
    var shape1 = THREE.SceneUtils.createMultiMaterialObject(
        new THREE.BoxGeometry( 50, 50, 50 ),
        multiMaterial );
    shape1.rotation.set(15,15,15);
    scene.add( shape1 );

    //Sphere
    var shape2 = THREE.SceneUtils.createMultiMaterialObject(
        new THREE.SphereGeometry( 40, 32, 16 ),
        multiMaterial );
    shape2.position.set(-100, 0, 0);
    scene.add( shape2 );

    //Torus knot
    var shape3 = THREE.SceneUtils.createMultiMaterialObject(
        // total knot radius, tube radius, number cylinder segments, sides per cyl. segment,
        //  p-loops around torus, q-loops around torus
        new THREE.TorusKnotGeometry( 30, 6, 160, 10, 3, 7 ),
        multiMaterial );
    shape3.position.set(100, 0, 0);
    shape3.rotation.set(15,30,0);
    scene.add( shape3 );

}

function animate() {
    requestAnimationFrame( animate );
    renderer.render( scene, camera );
    return renderer;
}