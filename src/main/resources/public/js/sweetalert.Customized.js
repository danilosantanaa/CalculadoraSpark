function confirmacao(titulo, mensagem, tipo) {
    return new Promise((resolve) => {
        Swal.fire({
            title: titulo,
            text: mensagem,
            icon: tipo,
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Confirmar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.value) {
                resolve(true);
            } else {
                resolve(false);
            }
        });
    });
}

function toast(titulo, mensagem, tipo) {
    Swal.fire({
        title: titulo,
        text: mensagem,
        icon: tipo,
        toast: true,
        position: "bottom-end",
        showConfirmButton: false
    });
}

function toastTimed(titulo, mensagem, tipo) {
    Swal.fire({
        title: titulo,
        text: mensagem,
        icon: tipo,
        toast: true,
        position: "top-end",
        timer: 3000,
        timerProgressBar: true,
        showConfirmButton: false,
        didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer)
            toast.addEventListener('mouseleave', Swal.resumeTimer)
        }
    });
}