var  s3utils={
		c2v:function (aStr) {
			if (aStr == null)
				return null;
			var fStr, result;
			fStr = "" + aStr;
			result = new Array(fStr.length);
			for ( var i = 0; i < fStr.length; i++) {
				switch (fStr.charCodeAt(i)) {
				case 34:
					result.push("&quot;");
					break;
				case 38:
					result.push("&amp;");
					break;
				case 39:
					result.push("&apos;");
					break;
				case 92:
					result.push("&#92");
					break;
				default:
					result.push(fStr.charAt(i));
					break;
				}
			}

			return result.join("");
		},
		c2h:function(aStr) {
			if (aStr == null)
				return null;
			var fStr, result;
			fStr = "" + aStr;
			result = new Array(fStr.length);
			for ( var i = 0; i < fStr.length; i++) {
				switch (fStr.charCodeAt(i)) {
				case 34:
					result.push("&quot;");
					break;
				case 38:
					result.push("&amp;");
					break;
				case 39:
					result.push("&apos;");
					break;
				case 92:
					result.push("&#92");
					break;
				case 60:// <
					result.push("&lt;");
					break;
				case 62:
					result.push("&gt;");
					break;
				default:
					result.push(fStr.charAt(i));
					break;
				}
			}

			return result.join("");
		}
};
