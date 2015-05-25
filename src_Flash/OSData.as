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

// OSData.as
package {
   
    import flash.display.Sprite;
    import flash.text.Font;
    import flash.text.FontType;
    import flash.text.FontStyle;
    import flash.external.ExternalInterface;
	import flash.system.Capabilities;
   
    public class OSData extends Sprite
    {
        public function OSData()
        {
		 ExternalInterface.addCallback('getFonts', this.getDeviceFonts);
 		 ExternalInterface.addCallback('getOS', this.getOS);
		 ExternalInterface.addCallback('getResolution', this.getResolution);
		 ExternalInterface.addCallback('getLanguage', this.getLanguage);

 		 if  (ExternalInterface.available) {
                	ExternalInterface.call("isConnected");
	         }
        }
       
        public function getDeviceFonts():Array
        {
            var embeddedAndDeviceFonts:Array = Font.enumerateFonts(true);
           
            var deviceFontNames:Array = [];
            for each (var font:Font in embeddedAndDeviceFonts)
            {
                if (font.fontType == FontType.EMBEDDED
                    || font.fontStyle != FontStyle.REGULAR
                    )
                    continue;
                deviceFontNames.push(font.fontName);
            }
           
            return deviceFontNames;
        }
		
		public function getOS():String
		{
			return Capabilities.os;
		}
		
		public function getResolution():Array
		{
			return [Capabilities.screenResolutionX,Capabilities.screenResolutionY];
		}

		public function getLanguage():String
		{
			return Capabilities.language;
		}
    }
}
