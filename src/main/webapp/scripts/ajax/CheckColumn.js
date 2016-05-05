Ext.grid.CheckColumn = function(config){
	Ext.apply(this, config);
		if(!this.id){
			this.id = Ext.id();
	}
	this.renderer = this.renderer.createDelegate(this);
};

Ext.grid.CheckColumn.prototype ={
	init : function(grid){
		this.grid = grid;
		this.grid.on('render', function(){
		var view = this.grid.getView();
		view.mainBody.on('mousedown', this.onMouseDown, this);
	}, this);
},

onMouseDown : function(e, t){

	if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1) {
		var row = this.grid.view.findRowIndex(t);
		var col = this.grid.view.findCellIndex(t);
		var r = this.grid.store.getAt(row);
		var field = this.grid.colModel.getDataIndex(col);
		var xe = {
			grid: this.grid,
			record: r,
			field: field,
			value: r.data[field],
			row: row,
			column: col,
			cancel:false
		};
		if(this.grid.fireEvent("beforeedit", xe, this.grid) !== false && xe.cancel !== true) {
			e.stopEvent();
			var index = this.grid.getView().findRowIndex(t);
			var record = this.grid.store.getAt(index);
			record.set(this.dataIndex, !record.data[this.dataIndex]);
		}
	}
},

renderer : function(v, p, record){
	p.css += ' x-grid3-check-col-td';
	return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'"> </div>';
}
};