const FLOWABLE_API = "http://localhost:8080/flowable-rest/service";
const AUTH_HEADER = "Basic " + btoa("rest-admin:MiPasswordSegura_Admin_2026!");

const listaTareas = document.getElementById("lista-tareas");
const modal = document.getElementById("modal-formulario");
const btnNuevoLead = document.getElementById("btn-nuevo-lead");
const btnRefrescar = document.getElementById("btn-refrescar");
const btnCerrar = document.querySelector(".cerrar");

// Escuchamos el clic del botón Guardar
const btnGuardar = document.getElementById("btn-guardar");
btnGuardar.addEventListener("click", guardarLead);

document.addEventListener("DOMContentLoaded", cargarTareas);
btnRefrescar.addEventListener("click", cargarTareas);
btnNuevoLead.addEventListener("click", () => modal.classList.remove("oculto"));
btnCerrar.addEventListener("click", () => modal.classList.add("oculto"));

async function guardarLead() {
    alert("1. ¡El botón funciona! Iniciando proceso...");
    
    const nombre = document.getElementById("nombreCliente").value;
    const compra = document.getElementById("quiereComprar").value === "true";

    try {
        const res = await fetch(`${FLOWABLE_API}/runtime/process-instances`, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
            body: JSON.stringify({ processDefinitionKey: "to_be_leads" })
        });
        
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        const proc = await res.json();
        alert("2. Proceso iniciado con ID: " + proc.id);

        const resTasks = await fetch(`${FLOWABLE_API}/runtime/tasks?processInstanceId=${proc.id}`, {
            headers: { "Authorization": AUTH_HEADER }
        });
        const tasks = await resTasks.json();
        
        if (tasks.data.length > 0) {
            const taskId = tasks.data[0].id;
            alert("3. Tarea encontrada. Completando...");

            const resComplete = await fetch(`${FLOWABLE_API}/runtime/tasks/${taskId}`, {
                method: "POST",
                headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "complete",
                    variables: [
                        { name: "quiereComprar", value: compra },
                        { name: "nombreCliente", value: nombre }
                    ]
                })
            });
            if (!resComplete.ok) throw new Error(`Error al completar: ${resComplete.status}`);
            alert("4. ¡Tarea completada con éxito!");
        }

        modal.classList.add("oculto");
        cargarTareas();
    } catch (error) {
        alert("❌ Hubo un error: " + error.message);
    }
}

async function cargarTareas() {
    listaTareas.innerHTML = '<p class="loading">Cargando...</p>';
    try {
        const res = await fetch(`${FLOWABLE_API}/runtime/tasks`, {
            headers: { "Authorization": AUTH_HEADER }
        });
        const data = await res.json();
        
        listaTareas.innerHTML = "";
        if (data.data.length === 0) {
            listaTareas.innerHTML = '<p>No hay tareas pendientes.</p>';
            return;
        }

        data.data.forEach(tarea => {
            const div = document.createElement("div");
            div.className = "tarea-card";
            div.innerHTML = `
                <div>
                    <h3>${tarea.name}</h3>
                    <small>ID: ${tarea.id.substring(0, 8)}...</small>
                </div>
                <button class="btn-completar" onclick="completarTarea('${tarea.id}', '${tarea.name}')">Completar</button>
            `;
            listaTareas.appendChild(div);
        });
    } catch (error) {
        listaTareas.innerHTML = `<p style="color:red">Error al cargar tareas.</p>`;
    }
}

async function completarTarea(taskId, taskName) {
    if(!confirm(`¿Completar la tarea "${taskName}"?`)) return;
    
    try {
        const res = await fetch(`${FLOWABLE_API}/runtime/tasks/${taskId}`, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
            body: JSON.stringify({ action: "complete" })
        });
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        cargarTareas();
    } catch (error) {
        alert("Error al completar: " + error.message);
    }
}