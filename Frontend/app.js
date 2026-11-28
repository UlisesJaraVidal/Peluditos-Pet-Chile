const FLOWABLE_API = "http://localhost:8080/flowable-rest/service";
const AUTH_HEADER = "Basic " + btoa(window.APP_CONFIG.flowableUser + ":" + window.APP_CONFIG.flowablePass);

const listaTareas = document.getElementById("lista-tareas");
const modal = document.getElementById("modal-formulario");
const btnNuevoLead = document.getElementById("btn-nuevo-lead");
const btnNuevaCampana = document.getElementById("btn-nueva-campana");
const btnRefrescar = document.getElementById("btn-refrescar");
const btnCerrar = document.querySelector(".cerrar");
const btnGuardar = document.getElementById("btn-guardar");

// Eventos
document.addEventListener("DOMContentLoaded", cargarTareas);
btnRefrescar.addEventListener("click", cargarTareas);
btnNuevoLead.addEventListener("click", () => modal.classList.remove("oculto"));
btnCerrar.addEventListener("click", () => modal.classList.add("oculto"));
btnGuardar.addEventListener("click", guardarLead);
btnNuevaCampana.addEventListener("click", iniciarCampana);

// 1. Registrar un Lead (Proceso de Leads)
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
        // MENSAJE PROFESIONAL DE ÉXITO
        Swal.fire({
            title: '¡Cliente Registrado!',
            text: 'Los datos se guardaron correctamente y las tareas han sido generadas.',
            icon: 'success',
            confirmButtonColor: '#27ae60',
            confirmButtonText: 'Entendido'
        });
        cargarTareas();
    } catch (error) {
        Swal.fire('Error', 'No se pudo registrar el cliente: ' + error.message, 'error');
    }
}

// 2. Iniciar una Campaña (Proceso de Oportunidades)
async function iniciarCampana() {
    try {
        const res = await fetch(`${FLOWABLE_API}/runtime/process-instances`, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
            body: JSON.stringify({ processDefinitionKey: "tOBEOportunidades" })
        });

        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        const proc = await res.json();

        const resTasks = await fetch(`${FLOWABLE_API}/runtime/tasks?processInstanceId=${proc.id}`, {
            headers: { "Authorization": AUTH_HEADER }
        });
        const tasks = await resTasks.json();

        if (tasks.data.length > 0) {
            const taskId = tasks.data[0].id;
            await fetch(`${FLOWABLE_API}/runtime/tasks/${taskId}`, {
                method: "POST",
                headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
                body: JSON.stringify({ action: "complete" })
            });
        }

        // MENSAJE PROFESIONAL DE CAMPAÑA
        Swal.fire({
            title: '¡Campaña Generada!',
            text: 'Se ha creado una nueva oportunidad comercial en tu bandeja.',
            icon: 'info',
            confirmButtonColor: '#8e44ad',
            confirmButtonText: 'Ver Tareas'
        }).then(() => {
            cargarTareas();
        });

    } catch (error) {
        Swal.fire('Error', 'No se pudo generar la campaña: ' + error.message, 'error');
    }
}

// 3. Cargar Tareas en la Bandeja
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

            let nombreCliente = "Sin nombre";
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
                </div>
                <button class="btn-completar" onclick="completarTarea('${tarea.id}', '${tarea.name}')">Completar</button>
            `;
            listaTareas.appendChild(div);
        }
    } catch (error) {
        listaTareas.innerHTML = `<p style="color:red">Error al cargar tareas: ${error.message}</p>`;
    }
}

// 4. Completar Tarea (Maneja la lógica de Campaña)
async function completarTarea(taskId, taskName) {
    try {
        let variables = [];

        // Preguntamos si compró usando SweetAlert en lugar del confirm feo
        if (taskName.includes("Registrar resultado") || taskName.includes("campaña")) {
            const result = await Swal.fire({
                title: '¿El cliente compró?',
                text: "Indica si la campaña fue efectiva para este cliente.",
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#27ae60',
                cancelButtonColor: '#e74c3c',
                confirmButtonText: 'Sí, compró',
                cancelButtonText: 'No, no compró'
            });

            // result.isConfirmed será true si presionó "Sí", false si presionó "No"
            const compro = result.isConfirmed;
            variables.push({ name: "compro", value: compro });
        }

        const res = await fetch(`${FLOWABLE_API}/runtime/tasks/${taskId}`, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER, "Content-Type": "application/json" },
            body: JSON.stringify({
                action: "complete",
                variables: variables
            })
        });
        if (!res.ok) throw new Error(`Error HTTP: ${res.status}`);
        cargarTareas();
    } catch (error) {
        Swal.fire('Error', 'Error al completar tarea: ' + error.message, 'error');
    }
}