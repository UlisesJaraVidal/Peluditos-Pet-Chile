const FLOWABLE_API = "http://localhost:8080/flowable-rest/service";
const AUTH_HEADER = "Basic " + btoa(`${window.APP_CONFIG.flowableUser}:${window.APP_CONFIG.flowablePass}`);
const listaTareas = document.getElementById("lista-tareas");
const modal = document.getElementById("modal-formulario");
const btnNuevoLead = document.getElementById("btn-nuevo-lead");
const btnRefrescar = document.getElementById("btn-refrescar");
const btnCerrar = document.querySelector(".cerrar");
const btnGuardar = document.getElementById("btn-guardar");

btnGuardar.addEventListener("click", guardarLead);

document.addEventListener("DOMContentLoaded", cargarTareas);
btnRefrescar.addEventListener("click", cargarTareas);
btnNuevoLead.addEventListener("click", () => modal.classList.remove("oculto"));
btnCerrar.addEventListener("click", () => modal.classList.add("oculto"));

async function guardarLead() {
    const nombreCliente = document.getElementById("nombreCliente").value;
    const nombreMascota = document.getElementById("nombreMascota").value;
    const canalOrigen = document.getElementById("canalOrigen").value;
    const fechaCumple = document.getElementById("fechaCumple").value;
    const compra = document.getElementById("quiereComprar").value === "true";

    try {
        const res = await fetch(`${FLOWABLE_API}/runtime/process-instances`, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
            body: JSON.stringify({ processDefinitionKey: "to_be_leads" })
        });
        
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        const proc = await res.json();

        const resTasks = await fetch(`${FLOWABLE_API}/runtime/tasks?processInstanceId=${proc.id}`, {
            headers: { "Authorization": AUTH_HEADER }
        });
        const tasks = await resTasks.json();
        
        if (tasks.data.length > 0) {
            const taskId = tasks.data[0].id;
            
            const resComplete = await fetch(`${FLOWABLE_API}/runtime/tasks/${taskId}`, {
                method: "POST",
                headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
                body: JSON.stringify({
                    action: "complete",
                    variables: [
                        { name: "nombreCliente", value: nombreCliente },
                        { name: "nombreMascota", value: nombreMascota },
                        { name: "canalOrigen", value: canalOrigen },
                        { name: "fechaCumple", value: fechaCumple },
                        { name: "quiereComprar", value: compra }
                    ]
                })
            });
            if (!resComplete.ok) throw new Error(`Error al completar: ${resComplete.status}`);
        }

        modal.classList.add("oculto");
        cargarTareas();
    } catch (error) {
        alert("Error: " + error.message);
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

        for (const tarea of data.data) {
            const resVars = await fetch(`${FLOWABLE_API}/runtime/tasks/${tarea.id}/variables`, {
                headers: { "Authorization": AUTH_HEADER }
            });
            
            let nombreCliente = "Cliente sin nombre";
            if (resVars.ok) {
                const variables = await resVars.json();
                const varNombre = variables.find(v => v.name === 'nombreCliente');
                if (varNombre) nombreCliente = varNombre.value;
            }

            const div = document.createElement("div");
            div.className = "tarea-card";
            div.innerHTML = `
                <div>
                    <h3>${tarea.name}</h3>
                    <p style="margin: 5px 0; color: #555;">👤 Cliente: <strong>${nombreCliente}</strong></p>
                    <small>ID: ${tarea.id.substring(0, 8)}...</small>
                </div>
                <button class="btn-completar" onclick="completarTarea('${tarea.id}', '${tarea.name}')">Completar</button>
            `;
            listaTareas.appendChild(div);
        }
    } catch (error) {
        listaTareas.innerHTML = `<p style="color:red">Error al cargar tareas.</p>`;
    }
}

async function completarTarea(taskId, taskName) {
    try {
        let variables = [];
        const action = "complete";

        // Lógica para el Gateway de Oportunidades
        if (taskName.includes("Registrar resultado") || taskName.includes("campaña")) {
            const compro = confirm("¿El cliente concretó la compra en esta campaña?\n\nAceptar = SÍ compró\nCancelar = NO compró");
            variables.push({ name: "compro", value: compro });
        }

        const res = await fetch(`${FLOWABLE_API}/runtime/tasks/${taskId}`, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
            body: JSON.stringify({ 
                action: action,
                variables: variables
            })
        });
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        cargarTareas();
    } catch (error) {
        console.error(error);
    }
}