		function ControlSelect(selectID){
			this.select=document.getElementById(selectID);
			
			this.save=function(){
				var items = this.select.options;
				var length= items.length;
				saved = new Array();
				
				for (var i=0;i<length;i++){
					saved[i]={
						text:items[i].text,
						value:items[i].value
					};
				}
				return saved;
			}
			
			this.savedValue=this.save();
			
			this.reset=function(){
				this.removeAll();
				for (var i=0;i<this.savedValue.length;i++){
					this.append(this.savedValue[i].text, this.savedValue[i].value);
				}
			}
			this.append=function(text, value){
				var items = this.select.options;
				items[items.length]= new Option(text, value);
			};
			
			this.insert=function(text, value, before){
				var item = new Option(text,value);
				this.select.add(item,before);
			}
			
			this.remove=function(index){
				this.select.remove(index);
			}
			
			this.changeText=function(index, text){
				var item=this.select.options(index);
				item.text=text;
			}
			
			this.changeValue=function(index, value){
				var item=this.select.options(index);
				item.value=value;
			}
			
			this.getValueByText=function(text){
				var items = this.select.options;
				var length = items.length;
				for (var i=0;i<length;i++){
					if (items[i].text==text){
						return items[i].value;
					}
				}
			}
			
			this.getValueByIndex=function(index){
				return this.select.options[index].value;
			}
			
			this.getTextByValue=function (value){
				var items = this.select.options;
				var length=items.length;
				for (var i=0;i<length;i++){
					if (items[i].value==value)
						return items[i].text;
				}
			}
			
			this.selectIndex=function(){
				return this.select.selectIndex;
			}
			
			this.removeAll=function(){
				var length=this.select.options.length;
				for (var i=0;i<length;i++)
					this.select.remove(0);
			}
			this.each=function(callback){
				var items = this.select.options;
				var length=items.length;
				for (var i=0;i<length;i++){
					if (items[i].selected){
						callback(items[i].text, items[i].value);
					}
				}
			}
			this.removeSelect=function(reservportlet){
				var items = this.select.options;
				var reserv=false;
				var length=items.length;
				var index=0;
				for (var i=0;i<length;i++){
					if (items[index].selected){
						if (items[index].text!=reservportlet){
							this.select.remove(index);
						}else{
							reserv=true;
						}
					}else{
						index++;
					}
				}
				return reserv;
			}
			
			this.selectAll=function(){
				var items = this.select.options;
				var length=items.length;
				for (var i=0;i<length;i++){
					items[i].selected=true;
				}
			}
		}