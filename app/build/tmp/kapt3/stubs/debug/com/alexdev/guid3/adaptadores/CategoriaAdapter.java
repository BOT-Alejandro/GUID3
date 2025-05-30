package com.alexdev.guid3.adaptadores;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0014B\u0013\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J\u001c\u0010\r\u001a\u00020\b2\n\u0010\u000e\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000f\u001a\u00020\fH\u0016J\u001c\u0010\u0010\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\fH\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/alexdev/guid3/adaptadores/CategoriaAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/alexdev/guid3/adaptadores/CategoriaAdapter$CategoriaViewHolder;", "listaCategorias", "", "Lcom/alexdev/guid3/dataClasses/categorias;", "(Ljava/util/List;)V", "actualizarLista", "", "nuevaLista", "", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "CategoriaViewHolder", "app_debug"})
public final class CategoriaAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.alexdev.guid3.adaptadores.CategoriaAdapter.CategoriaViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.alexdev.guid3.dataClasses.categorias> listaCategorias;
    
    public CategoriaAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.alexdev.guid3.dataClasses.categorias> listaCategorias) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.alexdev.guid3.adaptadores.CategoriaAdapter.CategoriaViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.alexdev.guid3.adaptadores.CategoriaAdapter.CategoriaViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void actualizarLista(@org.jetbrains.annotations.NotNull()
    java.util.List<com.alexdev.guid3.dataClasses.categorias> nuevaLista) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/alexdev/guid3/adaptadores/CategoriaAdapter$CategoriaViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Lcom/alexdev/guid3/adaptadores/CategoriaAdapter;Landroid/view/View;)V", "nombreCategoria", "Landroid/widget/TextView;", "getNombreCategoria", "()Landroid/widget/TextView;", "app_debug"})
    public final class CategoriaViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView nombreCategoria = null;
        
        public CategoriaViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View itemView) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getNombreCategoria() {
            return null;
        }
    }
}