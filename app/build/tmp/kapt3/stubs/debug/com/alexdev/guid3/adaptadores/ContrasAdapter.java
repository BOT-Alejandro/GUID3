package com.alexdev.guid3.adaptadores;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0013B\u0013\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005J\b\u0010\n\u001a\u00020\u000bH\u0016J\u001c\u0010\f\u001a\u00020\b2\n\u0010\r\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u000bH\u0016J\u001c\u0010\u000f\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000bH\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/alexdev/guid3/adaptadores/ContrasAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/alexdev/guid3/adaptadores/ContrasAdapter$VistaContrasena;", "contrasenas", "", "Lcom/alexdev/guid3/dataClasses/contras;", "(Ljava/util/List;)V", "agregarContrasena", "", "nuevaContrasena", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "VistaContrasena", "app_debug"})
public final class ContrasAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.alexdev.guid3.adaptadores.ContrasAdapter.VistaContrasena> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.alexdev.guid3.dataClasses.contras> contrasenas = null;
    
    public ContrasAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.alexdev.guid3.dataClasses.contras> contrasenas) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.alexdev.guid3.adaptadores.ContrasAdapter.VistaContrasena onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.alexdev.guid3.adaptadores.ContrasAdapter.VistaContrasena holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void agregarContrasena(@org.jetbrains.annotations.NotNull()
    com.alexdev.guid3.dataClasses.contras nuevaContrasena) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u000f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\f\u00a8\u0006\u0011"}, d2 = {"Lcom/alexdev/guid3/adaptadores/ContrasAdapter$VistaContrasena;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Lcom/alexdev/guid3/adaptadores/ContrasAdapter;Landroid/view/View;)V", "imagenIcono", "Landroid/widget/ImageView;", "getImagenIcono", "()Landroid/widget/ImageView;", "textoContrasena", "Landroid/widget/TextView;", "getTextoContrasena", "()Landroid/widget/TextView;", "textoCorreo", "getTextoCorreo", "textoTitulo", "getTextoTitulo", "app_debug"})
    public final class VistaContrasena extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.widget.ImageView imagenIcono = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView textoTitulo = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView textoCorreo = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView textoContrasena = null;
        
        public VistaContrasena(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.ImageView getImagenIcono() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTextoTitulo() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTextoCorreo() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTextoContrasena() {
            return null;
        }
    }
}